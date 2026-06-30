package com.cajaclara.app.feature.purchases.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.purchases.domain.valueobject.PurchaseId

/**
 * A single line of a purchase: how many units of a product were bought and at what unit cost.
 * Snapshots the product name and the purchase unit cost at the time of the purchase.
 */
data class PurchaseLine(
    val id: Long = 0L,
    val purchaseId: PurchaseId,
    val productId: ProductId,
    val productNameSnapshot: String,
    val quantity: Quantity,
    val unitCost: Money,
) {
    val lineTotal: Money get() = unitCost * quantity.value
}
