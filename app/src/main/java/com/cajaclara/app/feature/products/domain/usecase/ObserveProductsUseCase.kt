package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import kotlinx.coroutines.flow.Flow

/** Observes the products matching [filter], reacting to any change. */
class ObserveProductsUseCase(
    private val repository: ProductRepository,
) {
    operator fun invoke(filter: ProductFilter = ProductFilter.ALL): Flow<List<Product>> =
        repository.observeProducts(filter)
}
