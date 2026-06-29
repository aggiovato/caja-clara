package com.cajaclara.app.feature.sales.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room row for a sale (section 12.5). Totals are denormalized and never edited. */
@Entity(
    tableName = "sales",
    indices = [Index("soldAt")],
)
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val soldAt: Long,
    val totalRevenueCents: Long,
    val totalCostCents: Long,
    val note: String?,
    val createdAt: Long,
)
