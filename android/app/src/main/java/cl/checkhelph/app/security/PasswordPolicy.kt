package cl.checkhelph.app.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Reglas de contraseña. Se validan en la app y deberán validarse también en el servidor. */
object PasswordPolicy {
    const val MIN_LENGTH = 10
    const val MAX_AGE_DAYS = 90L
    const val HISTORY_SIZE = 3
    const val MAX_FAILED_ATTEMPTS = 5
    const val LOCK_MINUTES = 15L
    private const val DAY_MS = 86_400_000L

    private val COMMON = setOf("password", "contraseña", "contrasena", "123456", "qwerty", "checkhelph", "admin")

    data class Rule(val label: String, val ok: Boolean)

    fun rules(password: String, email: String): List<Rule> {
        val local = email.substringBefore('@').lowercase()
        val lower = password.lowercase()
        return listOf(
            Rule("Al menos $MIN_LENGTH caracteres", password.length >= MIN_LENGTH),
            Rule("Una letra mayúscula", password.any { it.isUpperCase() }),
            Rule("Una letra minúscula", password.any { it.isLowerCase() }),
            Rule("Un número", password.any { it.isDigit() }),
            Rule("Un símbolo (por ejemplo ! # $ % *)", password.any { !it.isLetterOrDigit() && !it.isWhitespace() }),
            Rule(
                "No incluye tu correo ni palabras comunes",
                password.isNotEmpty() && (local.length < 3 || local !in lower) && COMMON.none { it in lower },
            ),
        )
    }

    fun isValid(password: String, email: String) = rules(password, email).all { it.ok }

    fun isExpired(changedAt: Long, now: Long) = now - changedAt >= MAX_AGE_DAYS * DAY_MS

    fun daysLeft(changedAt: Long, now: Long): Long = MAX_AGE_DAYS - (now - changedAt) / DAY_MS

    /** Contraseña temporal aleatoria que cumple todas las reglas. */
    fun generateTemporary(random: SecureRandom = SecureRandom()): String {
        val upper = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val lowerChars = "abcdefghijkmnpqrstuvwxyz"
        val digits = "23456789"
        val symbols = "#$%*!?"
        val all = upper + lowerChars + digits + symbols
        val chars = mutableListOf(
            upper[random.nextInt(upper.length)],
            lowerChars[random.nextInt(lowerChars.length)],
            digits[random.nextInt(digits.length)],
            symbols[random.nextInt(symbols.length)],
        )
        repeat(8) { chars += all[random.nextInt(all.length)] }
        chars.shuffle(random)
        return chars.joinToString("")
    }
}

/** Hash de contraseñas con PBKDF2-HMAC-SHA256 y sal aleatoria por usuario. */
object PasswordHasher {
    private const val ITERATIONS = 120_000
    private const val KEY_BITS = 256
    private val random = SecureRandom()

    fun hash(password: String): String {
        val salt = ByteArray(16).also { random.nextBytes(it) }
        val key = derive(password, salt, ITERATIONS)
        return "$ITERATIONS:${b64(salt)}:${b64(key)}"
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 3) return false
        val iterations = parts[0].toIntOrNull() ?: return false
        val expected = Base64.getDecoder().decode(parts[2])
        val actual = derive(password, Base64.getDecoder().decode(parts[1]), iterations)
        return MessageDigest.isEqual(expected, actual)
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun b64(bytes: ByteArray) = Base64.getEncoder().encodeToString(bytes)
}
