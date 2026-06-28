package com.cajaclara.app.feature.sales.domain.model

import com.cajaclara.app.core.money.Money
import java.time.Instant
import java.time.LocalDate

/**
 * A daily profit figure for a given [date] (section 11.7).
 *
 * Lets a shopkeeper log the day's profit by hand instead of registering every sale.
 * The [source] keeps manual and sales-derived figures apart so the daily balance never
 * double-counts (rule 15.6).
 */
data class DailyProfitEntry(
    val id: Long = 0L,
    val date: LocalDate,
    val amount: Money,
    val source: DailyProfitSource,
    val note: String? = null,
    val createdAt: Instant,
)
