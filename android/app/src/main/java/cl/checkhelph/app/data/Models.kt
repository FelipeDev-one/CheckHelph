package cl.checkhelph.app.data

enum class Role(val label: String) {
    JEFE("Jefe de local"),
    ASISTENTE("Asistente"),
    TRABAJADOR("Trabajador"),
    COLABORADOR("Colaborador"),
}

/** Momentos del turno. Los horarios están en [cl.checkhelph.app.domain.ShiftSchedule]. */
enum class Moment(val label: String) {
    APERTURA("Apertura"),
    INTERMEDIO("Intermedio"),
    CIERRE("Cierre"),
}

enum class QuestionCategory(val label: String) {
    TURNO("Del turno"),
    FRECUENTE("Frecuentes"),
    INGRESO("Ingreso"),
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    /** false = cuenta bloqueada (por ejemplo, persona desvinculada del local). */
    val active: Boolean,
    /** Formato "iteraciones:sal:hash" (PBKDF2). Nunca se guarda la contraseña en texto plano. */
    val passwordHash: String,
    val passwordChangedAt: Long,
    /** true tras una contraseña temporal: obliga a cambiarla al entrar. */
    val mustChangePassword: Boolean,
    val previousHashes: List<String> = emptyList(),
    val failedAttempts: Int = 0,
    val lockedUntil: Long = 0,
)

data class ResetCode(val email: String, val codeHash: String, val expiresAt: Long, val attempts: Int = 0)

data class Question(
    val id: String,
    val category: QuestionCategory,
    val moment: Moment?,
    val question: String,
    val answer: String,
    val uses: Int = 0,
)

/** Consulta sin respuesta que queda registrada para el Jefe de local. */
data class PendingQuery(
    val id: String,
    val text: String,
    val askedBy: String,
    val askedAt: Long,
)

data class AppData(
    val loaded: Boolean = false,
    val users: List<User> = emptyList(),
    val questions: List<Question> = emptyList(),
    val pending: List<PendingQuery> = emptyList(),
    val resetCodes: List<ResetCode> = emptyList(),
    val assistantCanSeeSchedules: Boolean = false,
    val sessionUserId: String? = null,
) {
    /** Usuario con sesión iniciada. Si lo bloquean, deja de existir y la app vuelve al inicio de sesión. */
    val currentUser: User? get() = users.firstOrNull { it.id == sessionUserId && it.active }
}
