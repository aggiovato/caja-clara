package com.cajaclara.app.feature.stock.data.mapper

import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.stock.data.local.entity.StockMovementEntity
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.model.StockMovementType
import java.time.Instant

fun StockMovementEntity.toDomain(): StockMovement = StockMovement(
    id = id,
    productId = ProductId(productId),
    type = StockMovementType.valueOf(type),
    quantity = Quantity(quantity),
    note = note,
    createdAt = Instant.ofEpochMilli(createdAt),
)

fun StockMovement.toEntity(): StockMovementEntity = StockMovementEntity(
    id = id,
    productId = productId.value,
    type = type.name,
    quantity = quantity.value,
    note = note,
    createdAt = createdAt.toEpochMilli(),
)
