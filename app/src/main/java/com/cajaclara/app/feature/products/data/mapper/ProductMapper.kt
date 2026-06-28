package com.cajaclara.app.feature.products.data.mapper

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.data.local.entity.ProductEntity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Instant

/** Maps a product row to the domain model. */
fun ProductEntity.toDomain(): Product = Product(
    id = ProductId(id),
    name = name,
    sku = sku,
    categoryId = categoryId?.let(::CategoryId),
    description = description,
    currentCost = Money.fromCents(currentCostCents),
    currentPvp = Money.fromCents(currentPvpCents),
    stockQuantity = Quantity(stockQuantity),
    status = ProductStatus.valueOf(status),
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
)

/** Maps the domain model to a product row. An UNSAVED id (0) lets Room autogenerate it. */
fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id.value,
    name = name,
    sku = sku,
    categoryId = categoryId?.value,
    description = description,
    currentCostCents = currentCost.cents,
    currentPvpCents = currentPvp.cents,
    stockQuantity = stockQuantity.value,
    status = status.name,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
)
