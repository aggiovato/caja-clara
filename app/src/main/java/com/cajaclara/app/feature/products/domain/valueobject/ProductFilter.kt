package com.cajaclara.app.feature.products.domain.valueobject

import com.cajaclara.app.feature.products.domain.model.ProductStatus

/**
 * Criteria for listing products (used by `observeProducts`).
 *
 * @param status filter by lifecycle status; `null` means all statuses (the "Todos" tab).
 * @param query free-text search over name/SKU; `null` or blank means no search.
 */
data class ProductFilter(
    val status: ProductStatus? = null,
    val query: String? = null,
) {
    companion object {
        /** No filtering: every product, no search. */
        val ALL = ProductFilter()
    }
}
