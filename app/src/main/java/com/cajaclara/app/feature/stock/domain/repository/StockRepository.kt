package com.cajaclara.app.feature.stock.domain.repository

import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import kotlinx.coroutines.flow.Flow

/** Port for stock-movement persistence. Implemented by Room in the data layer. */
interface StockRepository {

    /** Append a stock movement to the log. */
    suspend fun record(movement: StockMovement)

    /** Observe a product's stock movements, newest first. */
    fun observeMovements(productId: ProductId): Flow<List<StockMovement>>
}
