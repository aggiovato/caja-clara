package com.cajaclara.app.feature.purchases.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room row for a purchase (investment). Total is denormalized and never edited. */
@Entity(
    tableName = "purchases",
    indices = [Index("purchasedAt")],
)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchasedAt: Long,
    val totalInvestmentCents: Long,
    val note: String?,
    val createdAt: Long,
)
