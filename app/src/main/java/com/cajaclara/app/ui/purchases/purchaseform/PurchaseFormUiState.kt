package com.cajaclara.app.ui.purchases.purchaseform

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.model.Product

/** A line being drafted for the purchase: quantity, unit cost, and whether to update the cost. */
data class PurchaseDraft(val quantity: Int, val unitCost: Money, val updateCost: Boolean) {
    val lineTotal: Money get() = unitCost * quantity
}

/** A cart line resolved against its product (for display). */
data class PurchaseCartLine(val product: Product, val draft: PurchaseDraft) {
    val subtotal: Money get() = draft.lineTotal
}

/** State for the purchase form: products to pick from and the drafted cart. */
data class PurchaseFormUiState(
    val products: List<Product> = emptyList(),
    val cart: Map<Long, PurchaseDraft> = emptyMap(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
) {
    val lines: List<PurchaseCartLine> = products
        .filter { cart.containsKey(it.id.value) }
        .map { PurchaseCartLine(it, cart.getValue(it.id.value)) }

    val total: Money = lines.fold(Money.ZERO) { acc, l -> acc + l.subtotal }
    val hasItems: Boolean = cart.isNotEmpty()
}
