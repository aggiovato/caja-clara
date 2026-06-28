package com.cajaclara.app.feature.products.domain.repository

import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import kotlinx.coroutines.flow.Flow

/**
 * Port for product persistence (section 14). Implemented by Room in the data layer
 * (`RoomProductRepository`). The domain depends on this interface, never on Room.
 */
interface ProductRepository {

    /** Observe the products matching [filter], reacting to any change. */
    fun observeProducts(filter: ProductFilter): Flow<List<Product>>

    /** Fetch a single product, or `null` if it does not exist. */
    suspend fun getProduct(id: ProductId): Product?

    /** Persist a new product and return its assigned id. */
    suspend fun createProduct(product: Product): ProductId

    /** Persist changes to an existing product. */
    suspend fun updateProduct(product: Product)

    /** Append a price-change record to a product's history. */
    suspend fun addPriceHistory(entry: PriceHistoryEntry)
}
