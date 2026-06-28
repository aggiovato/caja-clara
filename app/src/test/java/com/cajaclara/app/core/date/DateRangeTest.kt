package com.cajaclara.app.core.date

import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateRangeTest {

    @Test
    fun `single day contains only that day`() {
        val day = LocalDate.of(2026, 6, 28)
        val range = DateRange.singleDay(day)
        assertTrue(day in range)
        assertFalse(day.minusDays(1) in range)
        assertFalse(day.plusDays(1) in range)
    }

    @Test
    fun `contains is inclusive on both ends`() {
        val range = DateRange(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30))
        assertTrue(LocalDate.of(2026, 6, 1) in range)
        assertTrue(LocalDate.of(2026, 6, 30) in range)
        assertTrue(LocalDate.of(2026, 6, 15) in range)
        assertFalse(LocalDate.of(2026, 5, 31) in range)
        assertFalse(LocalDate.of(2026, 7, 1) in range)
    }

    @Test
    fun `rejects end before start`() {
        assertThrows(IllegalArgumentException::class.java) {
            DateRange(LocalDate.of(2026, 6, 30), LocalDate.of(2026, 6, 1))
        }
    }
}
