package com.cajaclara.app.feature.purchases.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.cajaclara.app.feature.purchases.data.local.entity.PurchaseEntity
import com.cajaclara.app.feature.purchases.data.local.entity.PurchaseLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert
    suspend fun insertPurchase(purchase: PurchaseEntity): Long

    @Insert
    suspend fun insertLines(lines: List<PurchaseLineEntity>)

    /** Insert a purchase and its lines atomically, returning the assigned purchase id. */
    @Transaction
    suspend fun insertPurchaseWithLines(purchase: PurchaseEntity, lines: List<PurchaseLineEntity>): Long {
        val purchaseId = insertPurchase(purchase)
        insertLines(lines.map { it.copy(purchaseId = purchaseId) })
        return purchaseId
    }

    @Query("SELECT * FROM purchases WHERE purchasedAt >= :startMillis AND purchasedAt < :endMillis ORDER BY purchasedAt DESC")
    fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<PurchaseEntity>>

    /** Total investment across all purchases ever (for the business account balance). */
    @Query("SELECT COALESCE(SUM(totalInvestmentCents), 0) FROM purchases")
    fun observeTotalInvestment(): Flow<Long>

    @Query("SELECT * FROM purchase_lines WHERE purchaseId = :purchaseId")
    suspend fun linesForPurchase(purchaseId: Long): List<PurchaseLineEntity>
}
