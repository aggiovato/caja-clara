package com.cajaclara.app.feature.products.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room row for a price change (section 12.3). Cascades when its product is removed. */
@Entity(
    tableName = "price_history",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("productId")],
)
data class PriceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val oldCostCents: Long?,
    val newCostCents: Long,
    val oldPvpCents: Long?,
    val newPvpCents: Long,
    val reason: String?,
    val createdAt: Long,
)
