package com.cajaclara.app.ui.sales.sales

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.model.Sale

/** One line of the in-progress cart: a product and the chosen quantity. */
data class CartLine(val product: Product, val quantity: Int) {
    val subtotal: Money get() = product.currentPvp * quantity
}

/** State for the quick-sale screen: active products to pick from and the current cart. */
data class SalesUiState(
    val products: List<Product> = emptyList(),
    val cart: Map<Long, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val justSold: Boolean = false,
    // Today's sales history and cash close (shown in the bottom sheet).
    val dailySales: List<Sale> = emptyList(),
    val cashClose: CashClose? = null,
) {
    /** Expected total from today's registered sales (the day's forecast). */
    val dailyExpected: Money = dailySales.fold(Money.ZERO) { acc, s -> acc + s.totalRevenue }

    /** Whether today's cash has already been closed. */
    val isDayClosed: Boolean = cashClose != null
    val lines: List<CartLine> = products
        .filter { cart.containsKey(it.id.value) }
        .map { CartLine(it, cart.getValue(it.id.value)) }

    val total: Money = lines.fold(Money.ZERO) { acc, l -> acc + l.subtotal }
    val itemCount: Int = cart.values.sum()
    val hasItems: Boolean = cart.isNotEmpty()

    /** Units of [productId] currently in the cart. */
    fun quantityOf(productId: Long): Int = cart[productId] ?: 0
}
