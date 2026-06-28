package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [ProductRepository] for use-case tests. Records calls for assertions. */
class FakeProductRepository : ProductRepository {

    val stored = linkedMapOf<Long, Product>()
    val priceHistory = mutableListOf<PriceHistoryEntry>()
    var lastFilter: ProductFilter? = null
        private set

    private var nextId = 1L
    private val products = MutableStateFlow<List<Product>>(emptyList())

    /** Seed a product as if already persisted, returning its id. */
    fun seed(product: Product): ProductId {
        val id = if (product.id.isSaved) product.id.value else nextId++
        val saved = product.copy(id = ProductId(id))
        stored[id] = saved
        products.value = stored.values.toList()
        return ProductId(id)
    }

    override fun observeProducts(filter: ProductFilter): Flow<List<Product>> {
        lastFilter = filter
        return products
    }

    override suspend fun getProduct(id: ProductId): Product? = stored[id.value]

    override suspend fun createProduct(product: Product): ProductId = seed(product)

    override suspend fun updateProduct(product: Product) {
        stored[product.id.value] = product
        products.value = stored.values.toList()
    }

    override suspend fun addPriceHistory(entry: PriceHistoryEntry) {
        priceHistory.add(entry)
    }
}
