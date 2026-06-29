package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Clock

/**
 * Soft-deletes a product: marks it ARCHIVED so it disappears from every list, while its price
 * history and sales stay intact. Reversible via [ResumeProductUseCase].
 */
class ArchiveProductUseCase(
    private val repository: ProductRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId) {
        val product = repository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")
        repository.updateProduct(product.copy(status = ProductStatus.ARCHIVED, updatedAt = clock.instant()))
    }
}
