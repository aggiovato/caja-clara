package com.cajaclara.app.feature.sales.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.cajaclara.app.feature.sales.data.local.entity.SaleEntity
import com.cajaclara.app.feature.sales.data.local.entity.SaleLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert
    suspend fun insertLines(lines: List<SaleLineEntity>)

    /** Insert a sale and its lines atomically, returning the assigned sale id. */
    @Transaction
    suspend fun insertSaleWithLines(sale: SaleEntity, lines: List<SaleLineEntity>): Long {
        val saleId = insertSale(sale)
        insertLines(lines.map { it.copy(saleId = saleId) })
        return saleId
    }

    @Query("SELECT * FROM sales WHERE soldAt >= :startMillis AND soldAt < :endMillis ORDER BY soldAt DESC")
    fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sale_lines WHERE saleId = :saleId")
    suspend fun linesForSale(saleId: Long): List<SaleLineEntity>
}
