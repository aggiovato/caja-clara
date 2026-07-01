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

    /** Total revenue across all sales ever (for the business account balance). */
    @Query("SELECT COALESCE(SUM(totalRevenueCents), 0) FROM sales")
    fun observeTotalRevenue(): Flow<Long>

    /** Total cost of goods sold across all sales ever. */
    @Query("SELECT COALESCE(SUM(totalCostCents), 0) FROM sales")
    fun observeTotalCost(): Flow<Long>

    /** The best-selling product by units sold across all sales. */
    @Query(
        """
        SELECT productNameSnapshot AS name,
               SUM(quantity) AS units,
               SUM((unitPvpCents - unitCostCents) * quantity) AS profitCents
        FROM sale_lines GROUP BY productId ORDER BY units DESC LIMIT 1
        """,
    )
    fun observeTopSelling(): Flow<ProductAggRow?>

    /** The most profitable product by total profit generated across all sales. */
    @Query(
        """
        SELECT productNameSnapshot AS name,
               SUM(quantity) AS units,
               SUM((unitPvpCents - unitCostCents) * quantity) AS profitCents
        FROM sale_lines GROUP BY productId ORDER BY profitCents DESC LIMIT 1
        """,
    )
    fun observeMostProfitable(): Flow<ProductAggRow?>

    /** Total units sold (sum of line quantities) within the time window. */
    @Query(
        """
        SELECT COALESCE(SUM(quantity), 0) FROM sale_lines
        WHERE saleId IN (SELECT id FROM sales WHERE soldAt >= :startMillis AND soldAt < :endMillis)
        """,
    )
    fun observeUnitsSoldBetween(startMillis: Long, endMillis: Long): Flow<Int>

    @Query("SELECT * FROM sale_lines WHERE saleId = :saleId")
    suspend fun linesForSale(saleId: Long): List<SaleLineEntity>
}
