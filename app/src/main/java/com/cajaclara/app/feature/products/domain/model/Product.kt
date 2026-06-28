package com.cajaclara.app.feature.products.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.Margin
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Instant

/**
 * A product the shop sells (section 11.1).
 *
 * Products are never physically deleted — see [ProductStatus]. The current [margin] is
 * derived from [currentCost]/[currentPvp], never stored, so it can never go stale.
 * Note: [stockQuantity] uses [Quantity] (the roadmap entity table types it as `Int`;
 * the Room mapper converts).
 */
data class Product(
    val id: ProductId,
    val name: String,
    val sku: String? = null,
    val categoryId: CategoryId? = null,
    val description: String? = null,
    /** Path to the saved product image on disk; null falls back to a category icon in the UI. */
    val imagePath: String? = null,
    val currentCost: Money,
    val currentPvp: Money,
    val stockQuantity: Quantity,
    val status: ProductStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    /** Margin derived from the current cost and price. */
    val margin: Margin get() = Margin(cost = currentCost, price = currentPvp)

    /** Whether the product is offered in quick sale (delegates to its status). */
    val isSellable: Boolean get() = status.isSellable
}
