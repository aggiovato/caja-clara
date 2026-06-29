package com.cajaclara.app.feature.stock.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cajaclara.app.feature.stock.data.local.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {

    @Insert
    suspend fun insert(entity: StockMovementEntity): Long

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY createdAt DESC")
    fun observeForProduct(productId: Long): Flow<List<StockMovementEntity>>
}
