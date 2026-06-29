package com.cajaclara.app.feature.stock.data.repository

import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.stock.data.local.dao.StockMovementDao
import com.cajaclara.app.feature.stock.data.mapper.toDomain
import com.cajaclara.app.feature.stock.data.mapper.toEntity
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room-backed [StockRepository]. */
class RoomStockRepository @Inject constructor(
    private val dao: StockMovementDao,
) : StockRepository {

    override suspend fun record(movement: StockMovement) {
        dao.insert(movement.toEntity())
    }

    override fun observeMovements(productId: ProductId): Flow<List<StockMovement>> =
        dao.observeForProduct(productId.value).map { rows -> rows.map { it.toDomain() } }
}
