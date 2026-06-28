package com.cajaclara.app.feature.sales.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import java.time.Instant

/**
 * A registered sale (section 11.5).
 *
 * Holds the denormalized totals (sum of its [SaleLine]s, computed once at registration
 * time and never edited afterwards). [totalProfit] is derived so it can never contradict
 * [totalRevenue]/[totalCost]. The individual lines are loaded separately by [id].
 */
data class Sale(
    val id: SaleId,
    val soldAt: Instant,
    val totalRevenue: Money,
    val totalCost: Money,
    val note: String? = null,
    val createdAt: Instant,
) {
    /** Derived so it can never contradict revenue/cost. */
    val totalProfit: Money get() = totalRevenue - totalCost
}
