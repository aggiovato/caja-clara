package com.cajaclara.app.feature.sales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cajaclara.app.feature.products.data.local.entity.ProductEntity

/**
 * Room row for one line of a sale (section 12.6). Cascades when its sale is removed, but the
 * product reference is RESTRICT: a product with sales can never be physically deleted, only
 * archived. Cost/price/name are snapshots taken at sale time.
 */
@Entity(
    tableName = "sale_lines",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("saleId"), Index("productId")],
)
data class SaleLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val productNameSnapshot: String,
    val quantity: Int,
    val unitCostCents: Long,
    val unitPvpCents: Long,
)
