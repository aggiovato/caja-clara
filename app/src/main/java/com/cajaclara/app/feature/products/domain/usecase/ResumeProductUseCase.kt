package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Clock

/**
 * Resumes a paused or archived product back into the catalog: ACTIVE if it has stock,
 * otherwise SOLD_OUT.
 */
class ResumeProductUseCase(
    private val repository: ProductRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId) {
        val product = repository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")
        val status = if (product.stockQuantity.isZero) ProductStatus.SOLD_OUT else ProductStatus.ACTIVE
        repository.updateProduct(product.copy(status = status, updatedAt = clock.instant()))
    }
}
