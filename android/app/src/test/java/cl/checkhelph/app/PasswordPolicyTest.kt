package cl.checkhelph.app

import cl.checkhelph.app.security.PasswordHasher
import cl.checkhelph.app.security.PasswordPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordPolicyTest {
    private val email = "persona@checkhelph.test"

    @Test fun exige_largo_mayuscula_minuscula_numero_y_simbolo() {
        assertTrue(PasswordPolicy.isValid("Vitrina#2026x", email))
        assertFalse(PasswordPolicy.isValid("Corta#1a", email))        // menos de 10
        assertFalse(PasswordPolicy.isValid("vitrina#2026x", email))   // sin mayúscula
        assertFalse(PasswordPolicy.isValid("VITRINA#2026X", email))   // sin minúscula
        assertFalse(PasswordPolicy.isValid("Vitrina#abcd", email))    // sin número
        assertFalse(PasswordPolicy.isValid("Vitrina2026xx", email))   // sin símbolo
    }

    @Test fun rechaza_correo_y_palabras_comunes() {
        assertFalse(PasswordPolicy.isValid("Persona#2026", email))
        assertFalse(PasswordPolicy.isValid("Password#2026", email))
    }

    @Test fun vence_a_los_90_dias() {
        val day = 86_400_000L
        assertFalse(PasswordPolicy.isExpired(0, 89 * day))
        assertTrue(PasswordPolicy.isExpired(0, 90 * day))
        assertEquals(80, PasswordPolicy.daysLeft(0, 10 * day))
    }

    @Test fun temporal_cumple_las_reglas() {
        repeat(50) { assertTrue(PasswordPolicy.isValid(PasswordPolicy.generateTemporary(), email)) }
    }

    @Test fun hash_con_sal_y_verificacion() {
        val a = PasswordHasher.hash("Vitrina#2026x")
        val b = PasswordHasher.hash("Vitrina#2026x")
        assertNotEquals(a, b) // sal distinta
        assertTrue(PasswordHasher.verify("Vitrina#2026x", a))
        assertFalse(PasswordHasher.verify("vitrina#2026x", a))
        assertFalse("Vitrina#2026x" in a)
    }
}
