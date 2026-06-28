package com.cajaclara.app.feature.sales.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId

/**
 * A single line of a sale (section 11.6).
 *
 * Snapshots cost, price and product name **at sale time**: changing a product's price
 * later must never alter past sales. Line totals are derived from those snapshots and
 * the [quantity], so they can never contradict the stored figures.
 */
data class SaleLine(
    val id: Long = 0L,
    val saleId: SaleId,
    val productId: ProductId,
    val productNameSnapshot: String,
    val quantity: Quantity,
    val unitCostSnapshot: Money,
    val unitPvpSnapshot: Money,
) {
    val lineRevenue: Money get() = unitPvpSnapshot * quantity.value
    val lineCost: Money get() = unitCostSnapshot * quantity.value
    val lineProfit: Money get() = lineRevenue - lineCost
}
