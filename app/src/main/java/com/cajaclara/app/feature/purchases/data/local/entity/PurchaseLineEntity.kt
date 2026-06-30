package com.cajaclara.app.feature.purchases.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cajaclara.app.feature.products.data.local.entity.ProductEntity

/**
 * Room row for one line of a purchase. Cascades when its purchase is removed; the product
 * reference is RESTRICT so a product with purchases can never be physically deleted.
 */
@Entity(
    tableName = "purchase_lines",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["purchaseId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("purchaseId"), Index("productId")],
)
data class PurchaseLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchaseId: Long,
    val productId: Long,
    val productNameSnapshot: String,
    val quantity: Int,
    val unitCostCents: Long,
)
