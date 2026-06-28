package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
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
)

/**
 * Creates a product after validating it. A new product starts [ProductStatus.ACTIVE].
 *
 * Validates that the name is not blank and that cost/price are not negative. A price
 * below cost is allowed (the UI warns and lets the user confirm); it is not blocked here.
 */
class CreateProductUseCase(
    private val repository: ProductRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(input: NewProduct): ProductId {
        require(input.name.isNotBlank()) { "Product name must not be blank" }
        require(!input.cost.isNegative) { "Cost must not be negative" }
        require(!input.pvp.isNegative) { "Price must not be negative" }

        val now = clock.instant()
        val product = Product(
            id = ProductId.UNSAVED,
            name = input.name.trim(),
            sku = input.sku?.trim()?.ifBlank { null },
            categoryId = input.categoryId,
            description = input.description?.trim()?.ifBlank { null },
            currentCost = input.cost,
            currentPvp = input.pvp,
            stockQuantity = input.stock,
            status = ProductStatus.ACTIVE,
            createdAt = now,
            updatedAt = now,
        )
        return repository.createProduct(product)
    }
}
