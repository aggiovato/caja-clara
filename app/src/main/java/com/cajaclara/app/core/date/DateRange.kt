package com.cajaclara.app.core.date

import java.time.LocalDate

/**
 * An inclusive range of calendar days `[start, end]`, used for stats and balances.
 * Both ends are inclusive; a single day is `start == end`.
 */
data class DateRange(
    val start: LocalDate,
    val end: LocalDate,
) {
    init {
        require(!end.isBefore(start)) { "DateRange end ($end) is before start ($start)" }
    }

    operator fun contains(date: LocalDate): Boolean =
        !date.isBefore(start) && !date.isAfter(end)

    companion object {
        /** A range covering a single day. */
        fun singleDay(date: LocalDate): DateRange = DateRange(date, date)
    }
}
