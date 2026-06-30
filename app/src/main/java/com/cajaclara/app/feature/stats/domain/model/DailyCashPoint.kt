package com.cajaclara.app.feature.stats.domain.model

import com.cajaclara.app.core.money.Money
import java.time.LocalDate

/**
 * One day's cash flow: money in from [salesRevenue] and money out as [purchaseInvestment].
 * [net] is the day's cash balance (in − out). This is cash flow, not sales profit.
 */
data class DailyCashPoint(
    val date: LocalDate,
    val salesRevenue: Money,
    val purchaseInvestment: Money,
) {
    val net: Money get() = salesRevenue - purchaseInvestment
}
