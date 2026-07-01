package com.cajaclara.app.feature.stats.domain.model

import com.cajaclara.app.core.money.Money

/** A product ranked by an aggregate over its sales (units sold and profit generated). */
data class TopProduct(
    val name: String,
    val units: Int,
    val profit: Money,
)

/**
 * Business health insights derived from all sales. [profitabilityPercent] is the overall margin
 * (total profit over total revenue); null when there are no sales yet. Both rankings are null
 * until at least one sale exists.
 */
data class BusinessInsights(
    val profitabilityPercent: Double? = null,
    val topSelling: TopProduct? = null,
    val mostProfitable: TopProduct? = null,
) {
    companion object {
        val EMPTY = BusinessInsights()
    }
}
