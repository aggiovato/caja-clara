package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Clock

/** Pauses a product: hidden from quick sale, kept in management. Reversible via [ResumeProductUseCase]. */
class PauseProductUseCase(
    private val repository: ProductRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId) {
        val product = repository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")
        repository.updateProduct(product.copy(status = ProductStatus.PAUSED, updatedAt = clock.instant()))
    }
}
