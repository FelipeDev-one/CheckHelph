package cl.checkhelph.app.data

/** Tareas y tickets de demostración (aún solo en memoria). */
data class DemoTask(
    val id: Int,
    val title: String,
    val area: String,
    val moment: Moment,
    val dueLabel: String?,
    val needsEvidence: Boolean,
    val done: Boolean = false,
)

enum class TicketType(val label: String) {
    MAQUINARIA("Falla de maquinaria"),
    LOCAL("Incidente del local"),
    ACCIDENTE("Accidente"),
    OTRO("Otro incidente"),
}

data class DemoTicket(val id: Int, val type: TicketType, val context: String, val status: String)

object DemoData {
    val tasks = listOf(
        DemoTask(1, "Revisar etiquetas de precio", "Sala de ventas", Moment.APERTURA, "10:30", needsEvidence = true),
        DemoTask(2, "Ordenar probadores", "Probadores", Moment.INTERMEDIO, null, needsEvidence = false),
        DemoTask(3, "Reponer tallas en mesa central", "Sala de ventas", Moment.INTERMEDIO, "15:00", needsEvidence = true),
        DemoTask(4, "Cuadrar caja", "Caja", Moment.CIERRE, "21:00", needsEvidence = true),
        DemoTask(5, "Revisar alarmas de salida", "Acceso", Moment.APERTURA, null, needsEvidence = false, done = true),
    )

    val tickets = listOf(
        DemoTicket(1, TicketType.MAQUINARIA, "Lector de código de caja 2 no lee", "Abierto"),
        DemoTicket(2, TicketType.LOCAL, "Luz de probador 3 parpadea", "En revisión"),
        DemoTicket(3, TicketType.MAQUINARIA, "Impresora de boletas sin papel de repuesto", "Cerrado"),
    )
}
