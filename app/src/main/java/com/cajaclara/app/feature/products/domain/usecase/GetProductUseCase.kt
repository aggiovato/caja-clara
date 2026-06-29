package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId

/** Loads a single product by id (for the edit form), or null if it does not exist. */
class GetProductUseCase(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(id: ProductId): Product? = repository.getProduct(id)
}
