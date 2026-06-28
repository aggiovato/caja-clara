package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Clock

/**
 * Updates a product's current cost (section 15.2).
 *
 * Validates cost is not negative, then — only if the cost actually changed — updates the
 * product and appends a [PriceHistoryEntry]. Past sales are never touched (their snapshots
 * keep the old figures).
 */
class UpdateProductCostUseCase(
    private val repository: ProductRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId, newCost: Money, reason: String? = null) {
        require(!newCost.isNegative) { "Cost must not be negative" }
        val product = repository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")

        if (product.currentCost == newCost) return // no real change: no update, no history

        val now = clock.instant()
        repository.updateProduct(product.copy(currentCost = newCost, updatedAt = now))
        repository.addPriceHistory(
            PriceHistoryEntry(
                productId = productId,
                oldCost = product.currentCost,
                newCost = newCost,
                oldPvp = product.currentPvp,
                newPvp = product.currentPvp,
                reason = reason,
                createdAt = now,
            ),
        )
    }
}
