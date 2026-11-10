package cl.checkhelph.app.data

import cl.checkhelph.app.security.PasswordHasher

/**
 * Datos iniciales ficticios de la maqueta. Las cuentas y la contraseña de demostración
 * están documentadas en android/README.md. No usar datos reales ni RUT.
 */
object DemoSeed {
    const val DEMO_PASSWORD = "Turno#Demo2026"
    private const val DAY = 86_400_000L

    fun create(now: Long): AppData {
        fun user(id: String, name: String, email: String, role: Role, changedDaysAgo: Long = 10) = User(
            id = id, name = name, email = email, role = role, active = true,
            passwordHash = PasswordHasher.hash(DEMO_PASSWORD),
            passwordChangedAt = now - changedDaysAgo * DAY, mustChangePassword = false,
        )
        val users = listOf(
            user("u-jefe", "Camila Jefa (demo)", "jefe@checkhelph.test", Role.JEFE),
            user("u-asis", "Andrés Asistente (demo)", "asistente@checkhelph.test", Role.ASISTENTE),
            user("u-trab", "Tomás Trabajador (demo)", "trabajador@checkhelph.test", Role.TRABAJADOR),
            // Contraseña vencida a propósito, para mostrar el cambio obligatorio a los 90 días.
            user("u-cola", "Carla Colaboradora (demo)", "colaborador@checkhelph.test", Role.COLABORADOR, changedDaysAgo = 95),
            user("u-ex", "Ex integrante (demo)", "desvinculado@checkhelph.test", Role.TRABAJADOR).copy(active = false),
        )
        var n = 0
        fun q(cat: QuestionCategory, moment: Moment?, question: String, answer: String, uses: Int) =
            Question("q-${++n}", cat, moment, question, answer, uses)
        val T = QuestionCategory.TURNO
        val questions = listOf(
            q(T, Moment.APERTURA, "¿Cómo abro la caja?", "Ingresa con tu usuario, cuenta el fondo inicial y regístralo antes de la primera venta.", 14),
            q(T, Moment.APERTURA, "¿Qué reviso al abrir el local?", "Luces de vitrina, alarmas de salida, orden de sala y que los probadores estén limpios.", 9),
            q(T, Moment.APERTURA, "¿Dónde dejo mis cosas al llegar?", "En tu casillero de la sala de personal; no se permiten bolsos en sala de ventas.", 6),
            q(T, Moment.INTERMEDIO, "¿Dónde dejo las prendas de probador?", "En el carro de retorno junto a probadores; se reponen cada hora.", 11),
            q(T, Moment.INTERMEDIO, "¿Cómo pido mi colación?", "Coordina con la jefatura del turno; las colaciones se escalonan para no dejar la sala sola.", 8),
            q(T, Moment.INTERMEDIO, "¿Qué hago si falta una talla en sala?", "Revisa la bodega por código; si no hay stock, ofrece otra tienda o el canal online.", 5),
            q(T, Moment.CIERRE, "¿Cómo cierro el local?", "Cuadra caja, revisa probadores y baños, apaga luces de vitrina y activa la alarma.", 12),
            q(T, Moment.CIERRE, "¿Cómo cuadro la caja?", "Imprime el cierre, cuenta efectivo y vouchers, y registra cualquier diferencia antes de salir.", 10),
            q(T, Moment.CIERRE, "¿A qué hora cierra el local hoy?", "Lunes, viernes, sábado y último día del mes a las 21:30; los demás días a las 21:00.", 7),
            q(T, null, "¿Qué hago si suena la alarma al salir un cliente?", "Acércate con amabilidad, ofrece revisar la etiqueta y avisa a la jefatura si hay dudas.", 4),
            q(QuestionCategory.FRECUENTE, null, "¿Cuál es la política de cambios?", "Cambios dentro de 30 días con boleta y etiqueta.", 6),
            q(QuestionCategory.FRECUENTE, null, "¿Qué hago ante un reclamo de un cliente?", "Escucha, registra el caso y deriva a la jefatura si no puedes resolverlo.", 3),
            q(QuestionCategory.FRECUENTE, null, "¿Cómo reporto una falla de una máquina?", "Crea un ticket en la sección Tickets con el tipo de falla y un contexto breve.", 2),
            q(QuestionCategory.INGRESO, null, "¿Qué documentos debo traer el primer día?", "Pendiente de completar por la jefatura.", 0),
        )
        return AppData(users = users, questions = questions)
    }
}
