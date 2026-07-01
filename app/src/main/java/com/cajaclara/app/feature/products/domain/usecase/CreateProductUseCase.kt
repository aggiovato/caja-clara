package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.Margin
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.settings.domain.repository.SettingsRepository
import java.time.Clock

/** Data needed to create a product; the use case fills in id, status and timestamps. */
data class NewProduct(
    val name: String,
    val cost: Money,
    val pvp: Money,
    val stock: Quantity = Quantity.ZERO,
    val sku: String? = null,
    val categoryId: CategoryId? = null,
    val description: String? = null,
    val imagePath: String? = null,
)

/**
 * Creates a product after validating it.
 *
 * A product with no stock yet starts as [ProductStatus.SOLD_OUT] (you can create it before
 * actually having units); with stock it starts [ProductStatus.ACTIVE].
 *
 * Validates that the name is not blank, cost/price are not negative, and the margin respects
 * the configured minimum. When no minimum is set, a price below cost is allowed (the UI warns
 * and lets the user confirm).
 *
 * @throws MinMarginViolationException if the product's margin is below the configured minimum
 */
class CreateProductUseCase(
    private val repository: ProductRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(input: NewProduct): ProductId {
        require(input.name.isNotBlank()) { "Product name must not be blank" }
        require(!input.cost.isNegative) { "Cost must not be negative" }
        require(!input.pvp.isNegative) { "Price must not be negative" }

        settingsRepository.requireMinMargin(Margin(cost = input.cost, price = input.pvp))

        val now = clock.instant()
        val product = Product(
            id = ProductId.UNSAVED,
            name = input.name.trim(),
            sku = input.sku?.trim()?.ifBlank { null },
            categoryId = input.categoryId,
            description = input.description?.trim()?.ifBlank { null },
            imagePath = input.imagePath,
            currentCost = input.cost,
            currentPvp = input.pvp,
            stockQuantity = input.stock,
            status = if (input.stock.isZero) ProductStatus.SOLD_OUT else ProductStatus.ACTIVE,
            createdAt = now,
            updatedAt = now,
        )
        return repository.createProduct(product)
    }
}
