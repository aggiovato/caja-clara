package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.Margin
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.settings.domain.repository.SettingsRepository
import java.time.Clock

/**
 * Updates a product's current selling price (section 15.3).
 *
 * Validates the price is not negative and that the resulting margin respects the configured
 * minimum, then — only if the price actually changed — updates the product and appends a
 * [PriceHistoryEntry]. When no minimum margin is set, a price below cost is still allowed (the
 * UI warns and the user confirms). Past sales are never touched.
 *
 * @throws MinMarginViolationException if the new price drops the margin below the minimum
 */
class UpdateProductPvpUseCase(
    private val repository: ProductRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId, newPvp: Money, reason: String? = null) {
        require(!newPvp.isNegative) { "Price must not be negative" }
        val product = repository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")

        if (product.currentPvp == newPvp) return // no real change: no update, no history

        settingsRepository.requireMinMargin(Margin(cost = product.currentCost, price = newPvp))

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
