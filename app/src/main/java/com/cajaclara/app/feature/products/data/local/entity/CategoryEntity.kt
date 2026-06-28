package com.cajaclara.app.feature.products.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room row for a category (section 12.2). */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String?,
    val createdAt: Long,
)
