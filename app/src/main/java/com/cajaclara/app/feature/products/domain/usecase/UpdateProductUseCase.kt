package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Clock

/** General product fields (everything except cost/PVP/stock/status, which have their own actions). */
data class ProductEdits(
    val name: String,
    val sku: String? = null,
    val categoryId: CategoryId? = null,
    val description: String? = null,
    val imagePath: String? = null,
)

/**
 * Updates a product's general fields. Cost, PVP, stock and status are NOT touched here — those
 * go through their dedicated use cases (price history / stock movements / lifecycle actions).
 */
class UpdateProductUseCase(
    private val repository: ProductRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId, edits: ProductEdits) {
        require(edits.name.isNotBlank()) { "Product name must not be blank" }
        val product = repository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")

        repository.updateProduct(
            product.copy(
                name = edits.name.trim(),
                sku = edits.sku?.trim()?.ifBlank { null },
                categoryId = edits.categoryId,
                description = edits.description?.trim()?.ifBlank { null },
                imagePath = edits.imagePath,
                updatedAt = clock.instant(),
            ),
        )
    }
}
