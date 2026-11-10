package cl.checkhelph.app.domain

import cl.checkhelph.app.data.Moment
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Horario del local:
 * - Apertura 08:30–12:00
 * - Intermedio 12:00–18:00
 * - Cierre 18:00–21:30 los lunes, viernes, sábados y el último día del mes; 18:00–21:00 el resto.
 */
object ShiftSchedule {
    val OPEN: LocalTime = LocalTime.of(8, 30)
    val MIDDAY: LocalTime = LocalTime.of(12, 0)
    val EVENING: LocalTime = LocalTime.of(18, 0)
    private val LATE_CLOSE: LocalTime = LocalTime.of(21, 30)
    private val NORMAL_CLOSE: LocalTime = LocalTime.of(21, 0)
    private val LATE_DAYS = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
    private val HM = DateTimeFormatter.ofPattern("HH:mm")

    fun isLateClosing(date: LocalDate): Boolean =
        date.dayOfWeek in LATE_DAYS || date.dayOfMonth == date.lengthOfMonth()

    fun closingTime(date: LocalDate): LocalTime = if (isLateClosing(date)) LATE_CLOSE else NORMAL_CLOSE

    /** Momento del turno a esa hora, o null si el local está cerrado. */
    fun momentAt(dateTime: LocalDateTime): Moment? {
        val t = dateTime.toLocalTime()
        return when {
            t < OPEN -> null
            t < MIDDAY -> Moment.APERTURA
            t < EVENING -> Moment.INTERMEDIO
            t < closingTime(dateTime.toLocalDate()) -> Moment.CIERRE
            else -> null
        }
    }

    fun rangeLabel(moment: Moment, date: LocalDate): String = when (moment) {
        Moment.APERTURA -> "${OPEN.format(HM)}–${MIDDAY.format(HM)}"
        Moment.INTERMEDIO -> "${MIDDAY.format(HM)}–${EVENING.format(HM)}"
        Moment.CIERRE -> "${EVENING.format(HM)}–${closingTime(date).format(HM)}"
    }
}
