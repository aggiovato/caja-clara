package com.cajaclara.app.ui.products.products

import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter

/** State rendered by the products screen. Holds domain models; formatting happens in Compose. */
data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val filter: ProductFilter = ProductFilter.ALL,
    /** category id (value) -> name, to resolve each product's fallback icon. */
    val categoryNames: Map<Long, String> = emptyMap(),
    val isLoading: Boolean = true,
)
