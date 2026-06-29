package com.cajaclara.app.feature.sales.domain.model

import com.cajaclara.app.core.money.Money
import java.time.Instant
import java.time.LocalDate

/**
 * A cash close (cuadre de caja) for one day: the moment the shopkeeper counts the real cash and
 * confirms the day's profit. There is at most one per [date]; re-closing overwrites it.
 *
 * [expectedRevenue]/[expectedCost] are snapshots of the day's registered sales at close time.
 * [profit] comes from the sales (revenue − cost); [difference] is the cash control (counted −
 * expected): positive means surplus, negative means a shortfall.
 */
data class CashClose(
    val date: LocalDate,
    val expectedRevenue: Money,
    val expectedCost: Money,
    val countedCash: Money,
    val salesCount: Int,
    val closedAt: Instant,
    val note: String? = null,
) {
    /** Confirmed day profit, from registered sales. */
    val profit: Money get() = expectedRevenue - expectedCost

    /** Cash control: counted minus expected. Positive = surplus, negative = shortfall. */
    val difference: Money get() = countedCash - expectedRevenue

    val isBalanced: Boolean get() = difference.isZero
}
