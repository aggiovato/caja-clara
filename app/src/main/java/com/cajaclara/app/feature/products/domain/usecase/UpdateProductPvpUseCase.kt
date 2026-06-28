package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Clock

/**
 * Updates a product's current selling price (section 15.3).
 *
 * Validates price is not negative, then — only if the price actually changed — updates the
 * product and appends a [PriceHistoryEntry]. A price below cost is **allowed**: the UI warns
 * and the user confirms; this use case does not block it. Past sales are never touched.
 */
class UpdateProductPvpUseCase(
    private val repository: ProductRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId, newPvp: Money, reason: String? = null) {
        require(!newPvp.isNegative) { "Price must not be negative" }
        val product = repository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")

        if (product.currentPvp == newPvp) return // no real change: no update, no history

        val now = clock.instant()
        repository.updateProduct(product.copy(currentPvp = newPvp, updatedAt = now))
        repository.addPriceHistory(
            PriceHistoryEntry(
                productId = productId,
                oldCost = product.currentCost,
                newCost = product.currentCost,
                oldPvp = product.currentPvp,
                newPvp = newPvp,
                reason = reason,
                createdAt = now,
            ),
        )
    }
}
