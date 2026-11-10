package cl.checkhelph.app

import cl.checkhelph.app.data.Moment
import cl.checkhelph.app.domain.ShiftSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ShiftScheduleTest {
    // Noviembre 2026: lunes 9, martes 10, viernes 13, sábado 14, domingo 15, lunes 30 (último día).
    private fun at(day: Int, h: Int, m: Int) = LocalDateTime.of(2026, 11, day, h, m)

    @Test fun momentos_del_dia() {
        assertNull(ShiftSchedule.momentAt(at(10, 8, 29)))
        assertEquals(Moment.APERTURA, ShiftSchedule.momentAt(at(10, 8, 30)))
        assertEquals(Moment.APERTURA, ShiftSchedule.momentAt(at(10, 11, 59)))
        assertEquals(Moment.INTERMEDIO, ShiftSchedule.momentAt(at(10, 12, 0)))
        assertEquals(Moment.INTERMEDIO, ShiftSchedule.momentAt(at(10, 17, 59)))
        assertEquals(Moment.CIERRE, ShiftSchedule.momentAt(at(10, 18, 0)))
    }

    @Test fun cierre_normal_a_las_21() {
        assertEquals(Moment.CIERRE, ShiftSchedule.momentAt(at(10, 20, 59)))
        assertNull(ShiftSchedule.momentAt(at(10, 21, 0))) // martes
        assertNull(ShiftSchedule.momentAt(at(15, 21, 0))) // domingo
    }

    @Test fun cierre_extendido_lunes_viernes_sabado_y_fin_de_mes() {
        listOf(9, 13, 14, 30).forEach { day ->
            assertEquals("día $day", Moment.CIERRE, ShiftSchedule.momentAt(at(day, 21, 15)))
            assertNull("día $day", ShiftSchedule.momentAt(at(day, 21, 30)))
        }
        // 31 de diciembre (jueves) también es fin de mes.
        assertEquals(LocalTime.of(21, 30), ShiftSchedule.closingTime(LocalDate.of(2026, 12, 31)))
        // 28 de febrero de 2026 (sábado y fin de mes) y 26 de febrero (jueves normal).
        assertEquals(LocalTime.of(21, 0), ShiftSchedule.closingTime(LocalDate.of(2026, 2, 26)))
    }

    @Test fun etiqueta_de_cierre_segun_dia() {
        assertEquals("18:00–21:30", ShiftSchedule.rangeLabel(Moment.CIERRE, LocalDate.of(2026, 11, 13)))
        assertEquals("18:00–21:00", ShiftSchedule.rangeLabel(Moment.CIERRE, LocalDate.of(2026, 11, 10)))
    }
}
