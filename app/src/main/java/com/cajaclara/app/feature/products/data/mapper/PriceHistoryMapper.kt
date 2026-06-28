package com.cajaclara.app.feature.products.data.mapper

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.data.local.entity.PriceHistoryEntity
import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Instant

fun PriceHistoryEntity.toDomain(): PriceHistoryEntry = PriceHistoryEntry(
    id = id,
    productId = ProductId(productId),
    oldCost = oldCostCents?.let(Money::fromCents),
    newCost = Money.fromCents(newCostCents),
    oldPvp = oldPvpCents?.let(Money::fromCents),
    newPvp = Money.fromCents(newPvpCents),
    reason = reason,
    createdAt = Instant.ofEpochMilli(createdAt),
)

fun PriceHistoryEntry.toEntity(): PriceHistoryEntity = PriceHistoryEntity(
    id = id,
    productId = productId.value,
    oldCostCents = oldCost?.cents,
    newCostCents = newCost.cents,
    oldPvpCents = oldPvp?.cents,
    newPvpCents = newPvp.cents,
    reason = reason,
    createdAt = createdAt.toEpochMilli(),
)
