package com.cajaclara.app.feature.products.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cajaclara.app.feature.products.data.local.entity.PriceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {

    @Insert
    suspend fun insert(entity: PriceHistoryEntity): Long

    @Query("SELECT * FROM price_history WHERE productId = :productId ORDER BY createdAt ASC")
    fun observeForProduct(productId: Long): Flow<List<PriceHistoryEntity>>
}
