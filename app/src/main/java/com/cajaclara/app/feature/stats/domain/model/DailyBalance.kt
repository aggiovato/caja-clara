package com.cajaclara.app.feature.stats.domain.model

import com.cajaclara.app.core.money.Money
import java.time.LocalDate

/**
 * The profit balance for a single day (section 11.8). A computed model, not a table.
 *
 * Keeps sales-derived profit and manual profit apart and only combines them in
 * [totalProfit], so the two sources are never double-counted (rule 15.6). [salesProfit]
 * and [totalProfit] are derived so they can never contradict their inputs.
 */
data class DailyBalance(
    val date: LocalDate,
    val salesRevenue: Money,
    val salesCost: Money,
    val manualProfit: Money,
    val salesCount: Int,
    val productsSoldCount: Int,
) {
    /** Profit from registered sales: revenue minus cost. */
    val salesProfit: Money get() = salesRevenue - salesCost

    /** Day total: sales profit plus manually entered profit. */
    val totalProfit: Money get() = salesProfit + manualProfit
}
