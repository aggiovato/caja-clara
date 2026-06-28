package com.cajaclara.app.feature.products.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room row for a product (section 12.1). Primitive columns; mapping lives in the mapper. */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String?,
    val categoryId: Long?,
    val description: String?,
    val imagePath: String?,
    val currentCostCents: Long,
    val currentPvpCents: Long,
    val stockQuantity: Int,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
)
