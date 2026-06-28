package com.cajaclara.app.feature.products.data.repository

import com.cajaclara.app.feature.products.data.local.dao.PriceHistoryDao
import com.cajaclara.app.feature.products.data.local.dao.ProductDao
import com.cajaclara.app.feature.products.data.mapper.toDomain
import com.cajaclara.app.feature.products.data.mapper.toEntity
import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room-backed [ProductRepository]. Maps entities to/from the domain at the boundary. */
class RoomProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val priceHistoryDao: PriceHistoryDao,
) : ProductRepository {

    override fun observeProducts(filter: ProductFilter): Flow<List<Product>> =
        productDao.observeFiltered(
            status = filter.status?.name,
            query = filter.query?.takeIf { it.isNotBlank() },
        ).map { rows -> rows.map { it.toDomain() } }

    override suspend fun getProduct(id: ProductId): Product? =
        productDao.findById(id.value)?.toDomain()

    override suspend fun createProduct(product: Product): ProductId =
        ProductId(productDao.insert(product.toEntity()))

    override suspend fun updateProduct(product: Product) =
        productDao.update(product.toEntity())

    override suspend fun addPriceHistory(entry: PriceHistoryEntry) {
        priceHistoryDao.insert(entry.toEntity())
    }
}
