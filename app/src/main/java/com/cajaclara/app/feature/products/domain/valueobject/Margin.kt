package com.cajaclara.app.feature.products.domain.valueobject

import com.cajaclara.app.core.money.Money

/**
 * Centralizes a product's margin calculation from its cost and selling price
 * (section 15.1 of the roadmap).
 *
 * ```
 * unit margin    = price - cost
 * % on price     = (margin / price) * 100
 * markup on cost = (margin / cost)  * 100
 * ```
 *
 * It is a `data class` (not a `value class`) because it needs **two** values: [cost]
 * and [price]. Percentages are `Double` because they are display ratios, not money:
 * money stays in [Money] (cents as `Long`).
 */
data class Margin(
    val cost: Money,
    val price: Money,
) {
    /** Unit margin in money: `price - cost`. May be negative (loss). */
    val unitMargin: Money = price - cost

    /**
     * Margin percentage **over the selling price**. `null` when the price is 0
     * (cannot be spread over a zero-value sale).
     */
    val percentOnPrice: Double? =
        if (price.isZero) null
        else unitMargin.cents.toDouble() / price.cents.toDouble() * 100.0

    /**
     * Markup percentage **over the cost**. `null` when the cost is 0
     * (infinite markup: all profit).
     */
    val markupOnCost: Double? =
        if (cost.isZero) null
        else unitMargin.cents.toDouble() / cost.cents.toDouble() * 100.0

    /** Price is below cost: the app must warn before saving (rules 15.3). */
    val isBelowCost: Boolean get() = price < cost

    val isProfit: Boolean get() = unitMargin.isPositive
    val isLoss: Boolean get() = unitMargin.isNegative
    val isBreakEven: Boolean get() = unitMargin.isZero

    /** Estimated total profit if [units] units were sold at this margin. */
    fun estimatedProfitForStock(units: Int): Money = unitMargin * units
}
