package com.cajaclara.app.feature.stats.domain.model

import com.cajaclara.app.core.money.Money
import java.time.LocalDate

/**
 * One day's sales figures for the stats series: [revenue] and [cost] of what was sold that
 * day, with [profit] derived. (Cost here is the cost of goods sold; investment/purchases will
 * be a separate series once purchases exist.)
 */
data class DailySalesPoint(
    val date: LocalDate,
    val revenue: Money,
    val cost: Money,
) {
    val profit: Money get() = revenue - cost
}
