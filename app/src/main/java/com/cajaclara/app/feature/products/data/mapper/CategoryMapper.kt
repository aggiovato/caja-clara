package com.cajaclara.app.feature.products.data.mapper

import com.cajaclara.app.feature.products.data.local.entity.CategoryEntity
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import java.time.Instant

fun CategoryEntity.toDomain(): Category = Category(
    id = CategoryId(id),
    name = name,
    colorHex = colorHex,
    createdAt = Instant.ofEpochMilli(createdAt),
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id.value,
    name = name,
    colorHex = colorHex,
    createdAt = createdAt.toEpochMilli(),
)
