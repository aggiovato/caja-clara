package com.cajaclara.app.feature.stock.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room row for a stock movement (section 12.4). Primitive columns; mapping lives in the mapper. */
@Entity(
    tableName = "stock_movements",
    indices = [Index("productId")],
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val type: String,
    val quantity: Int,
    val note: String?,
    val createdAt: Long,
)
