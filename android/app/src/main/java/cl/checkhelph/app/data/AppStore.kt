package cl.checkhelph.app.data

import cl.checkhelph.app.security.PasswordHasher
import cl.checkhelph.app.security.PasswordPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.SecureRandom
import java.util.UUID

sealed interface LoginResult {
    data class Ok(val user: User) : LoginResult
    /** Contraseña temporal o vencida (más de 90 días): debe cambiarla antes de entrar. */
    data class MustChange(val user: User, val expired: Boolean) : LoginResult
    data object Invalid : LoginResult
    data object Blocked : LoginResult
    data class Locked(val minutes: Long) : LoginResult
}

/**
 * Almacenamiento local de la maqueta (archivo JSON en el almacenamiento privado de la app).
 *
 * IMPORTANTE: esto simula en el teléfono lo que hará el servidor en la versión real.
 * La seguridad efectiva (autenticación, bloqueo, recuperación por correo) debe vivir en el backend.
 */
class AppStore(private val file: File, private val now: () -> Long = System::currentTimeMillis) {
    private val _data = MutableStateFlow(AppData())
    val data: StateFlow<AppData> = _data
    private val random = SecureRandom()

    private val lock = Any()

    private fun update(change: (AppData) -> AppData): AppData = synchronized(lock) {
        val next = change(_data.value)
        _data.value = next
        save(next)
        next
    }

    private fun updateUser(id: String, change: (User) -> User) =
        update { d -> d.copy(users = d.users.map { if (it.id == id) change(it) else it }) }

    /** Carga los datos o crea los de demostración la primera vez. Llamar fuera del hilo principal. */
    fun init() {
        val loaded = if (file.exists()) runCatching { fromJson(JSONObject(file.readText())) }.getOrNull() else null
        val data = loaded ?: DemoSeed.create(now()).also { save(it) }
        _data.value = data.copy(loaded = true)
    }

    // ---------- Autenticación ----------

    fun login(email: String, password: String): LoginResult {
        val user = _data.value.users.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
            ?: return LoginResult.Invalid
        val t = now()
        if (user.lockedUntil > t) return LoginResult.Locked(((user.lockedUntil - t) / 60_000) + 1)

        if (!PasswordHasher.verify(password, user.passwordHash)) {
            val attempts = user.failedAttempts + 1
            if (attempts >= PasswordPolicy.MAX_FAILED_ATTEMPTS) {
                updateUser(user.id) { it.copy(failedAttempts = 0, lockedUntil = t + PasswordPolicy.LOCK_MINUTES * 60_000) }
                return LoginResult.Locked(PasswordPolicy.LOCK_MINUTES)
            }
            updateUser(user.id) { it.copy(failedAttempts = attempts) }
            return LoginResult.Invalid
        }
        // Solo se informa el bloqueo a quien conoce la contraseña, para no revelar qué correos existen.
        if (!user.active) return LoginResult.Blocked

        updateUser(user.id) { it.copy(failedAttempts = 0, lockedUntil = 0) }
        val expired = PasswordPolicy.isExpired(user.passwordChangedAt, t)
        if (user.mustChangePassword || expired) return LoginResult.MustChange(user, expired)
        update { it.copy(sessionUserId = user.id) }
        return LoginResult.Ok(user)
    }

    fun logout() = update { it.copy(sessionUserId = null) }

    /** Devuelve un mensaje de error o null si se cambió. [current] es null en el cambio forzado tras validar la contraseña. */
    fun changePassword(userId: String, current: String?, newPassword: String, startSession: Boolean): String? {
        val user = _data.value.users.firstOrNull { it.id == userId } ?: return "Usuario no encontrado."
        if (current != null && !PasswordHasher.verify(current, user.passwordHash)) return "La contraseña actual no es correcta."
        if (!PasswordPolicy.isValid(newPassword, user.email)) return "La nueva contraseña no cumple los requisitos."
        val reused = (listOf(user.passwordHash) + user.previousHashes).any { PasswordHasher.verify(newPassword, it) }
        if (reused) return "No puedes repetir tus últimas ${PasswordPolicy.HISTORY_SIZE} contraseñas."
        val hash = PasswordHasher.hash(newPassword)
        update { d ->
            d.copy(
                users = d.users.map {
                    if (it.id != userId) it else it.copy(
                        passwordHash = hash,
                        previousHashes = (listOf(it.passwordHash) + it.previousHashes).take(PasswordPolicy.HISTORY_SIZE),
                        passwordChangedAt = now(),
                        mustChangePassword = false,
                    )
                },
                sessionUserId = if (startSession) userId else d.sessionUserId,
            )
        }
        return null
    }

    /**
     * "Olvidé mi contraseña": genera un código de 6 dígitos válido por 15 minutos.
     * En la versión real se envía por correo; en esta maqueta se devuelve para mostrarlo en pantalla.
     */
    fun requestReset(email: String): String? {
        val user = _data.value.users.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) && it.active }
            ?: return null
        val code = (100_000 + random.nextInt(900_000)).toString()
        val entry = ResetCode(user.email, PasswordHasher.hash(code), now() + 15 * 60_000)
        update { d -> d.copy(resetCodes = d.resetCodes.filter { it.email != user.email } + entry) }
        return code
    }

    fun resetWithCode(email: String, code: String, newPassword: String): String? {
        val generic = "El código no es válido o venció. Solicita uno nuevo."
        val entry = _data.value.resetCodes.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) } ?: return generic
        if (entry.expiresAt < now() || entry.attempts >= 5) return generic
        if (!PasswordHasher.verify(code.trim(), entry.codeHash)) {
            update { d -> d.copy(resetCodes = d.resetCodes.map { if (it == entry) it.copy(attempts = it.attempts + 1) else it }) }
            return generic
        }
        val user = _data.value.users.first { it.email == entry.email }
        val error = changePassword(user.id, null, newPassword, startSession = false)
        if (error == null) {
            update { d -> d.copy(resetCodes = d.resetCodes - entry) }
            updateUser(user.id) { it.copy(failedAttempts = 0, lockedUntil = 0) }
        }
        return error
    }

    // ---------- Administración (Jefe de local) ----------

    /** Crea la cuenta con una contraseña temporal que debe cambiarse al primer ingreso. Devuelve (error, temporal). */
    fun createUser(name: String, email: String, role: Role): Pair<String?, String?> {
        val cleanEmail = email.trim().lowercase()
        if (name.isBlank()) return "Ingresa el nombre." to null
        if (!Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(cleanEmail)) return "Correo no válido." to null
        if (_data.value.users.any { it.email == cleanEmail }) return "Ya existe una cuenta con ese correo." to null
        val temp = PasswordPolicy.generateTemporary(random)
        val user = User(
            id = UUID.randomUUID().toString(), name = name.trim(), email = cleanEmail, role = role, active = true,
            passwordHash = PasswordHasher.hash(temp), passwordChangedAt = now(), mustChangePassword = true,
        )
        update { it.copy(users = it.users + user) }
        return null to temp
    }

    /** Bloquea o reactiva una cuenta. Al bloquearla, su sesión deja de ser válida de inmediato. */
    fun setActive(userId: String, active: Boolean) = updateUser(userId) { it.copy(active = active, failedAttempts = 0, lockedUntil = 0) }

    /** El Jefe asiste en la recuperación: asigna una contraseña temporal. */
    fun adminResetPassword(userId: String): String {
        val temp = PasswordPolicy.generateTemporary(random)
        updateUser(userId) { it.copy(passwordHash = PasswordHasher.hash(temp), mustChangePassword = true, failedAttempts = 0, lockedUntil = 0) }
        return temp
    }

    fun setAssistantCanSeeSchedules(value: Boolean) = update { it.copy(assistantCanSeeSchedules = value) }

    // ---------- Preguntas ----------

    fun registerUse(questionId: String) =
        update { d -> d.copy(questions = d.questions.map { if (it.id == questionId) it.copy(uses = it.uses + 1) else it }) }

    fun addPending(text: String, askedBy: String) = update { d ->
        if (d.pending.any { it.text.equals(text.trim(), ignoreCase = true) }) d
        else d.copy(pending = d.pending + PendingQuery(UUID.randomUUID().toString(), text.trim(), askedBy, now()))
    }

    fun answerPending(pendingId: String, question: String, answer: String, category: QuestionCategory, moment: Moment?) = update { d ->
        val q = Question(UUID.randomUUID().toString(), category, moment, question.trim(), answer.trim())
        d.copy(questions = d.questions + q, pending = d.pending.filter { it.id != pendingId })
    }

    fun dismissPending(pendingId: String) = update { d -> d.copy(pending = d.pending.filter { it.id != pendingId }) }

    // ---------- Persistencia JSON ----------

    private fun save(d: AppData) {
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(toJson(d).toString())
        if (!tmp.renameTo(file)) { file.delete(); tmp.renameTo(file) }
    }

    private fun toJson(d: AppData) = JSONObject().apply {
        put("users", JSONArray(d.users.map { u ->
            JSONObject()
                .put("id", u.id).put("name", u.name).put("email", u.email).put("role", u.role.name)
                .put("active", u.active).put("passwordHash", u.passwordHash).put("passwordChangedAt", u.passwordChangedAt)
                .put("mustChangePassword", u.mustChangePassword).put("previousHashes", JSONArray(u.previousHashes))
                .put("failedAttempts", u.failedAttempts).put("lockedUntil", u.lockedUntil)
        }))
        put("questions", JSONArray(d.questions.map { q ->
            JSONObject().put("id", q.id).put("category", q.category.name).put("moment", q.moment?.name ?: "")
                .put("question", q.question).put("answer", q.answer).put("uses", q.uses)
        }))
        put("pending", JSONArray(d.pending.map { p ->
            JSONObject().put("id", p.id).put("text", p.text).put("askedBy", p.askedBy).put("askedAt", p.askedAt)
        }))
        put("resetCodes", JSONArray(d.resetCodes.map { r ->
            JSONObject().put("email", r.email).put("codeHash", r.codeHash).put("expiresAt", r.expiresAt).put("attempts", r.attempts)
        }))
        put("assistantCanSeeSchedules", d.assistantCanSeeSchedules)
        put("sessionUserId", d.sessionUserId ?: "")
    }

    private fun <T> JSONArray.mapObjects(f: (JSONObject) -> T): List<T> = (0 until length()).map { f(getJSONObject(it)) }

    private fun fromJson(o: JSONObject) = AppData(
        users = o.getJSONArray("users").mapObjects { u ->
            val prev = u.optJSONArray("previousHashes") ?: JSONArray()
            User(
                id = u.getString("id"), name = u.getString("name"), email = u.getString("email"),
                role = Role.valueOf(u.getString("role")), active = u.getBoolean("active"),
                passwordHash = u.getString("passwordHash"), passwordChangedAt = u.getLong("passwordChangedAt"),
                mustChangePassword = u.getBoolean("mustChangePassword"),
                previousHashes = (0 until prev.length()).map { prev.getString(it) },
                failedAttempts = u.optInt("failedAttempts"), lockedUntil = u.optLong("lockedUntil"),
            )
        },
        questions = o.getJSONArray("questions").mapObjects { q ->
            Question(
                id = q.getString("id"), category = QuestionCategory.valueOf(q.getString("category")),
                moment = q.optString("moment").takeIf { it.isNotEmpty() }?.let { Moment.valueOf(it) },
                question = q.getString("question"), answer = q.getString("answer"), uses = q.optInt("uses"),
            )
        },
        pending = o.getJSONArray("pending").mapObjects { p ->
            PendingQuery(p.getString("id"), p.getString("text"), p.getString("askedBy"), p.getLong("askedAt"))
        },
        resetCodes = o.getJSONArray("resetCodes").mapObjects { r ->
            ResetCode(r.getString("email"), r.getString("codeHash"), r.getLong("expiresAt"), r.optInt("attempts"))
        },
        assistantCanSeeSchedules = o.optBoolean("assistantCanSeeSchedules"),
        sessionUserId = o.optString("sessionUserId").takeIf { it.isNotEmpty() },
    )
}
