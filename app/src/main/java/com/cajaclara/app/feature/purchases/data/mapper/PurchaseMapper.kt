package com.cajaclara.app.feature.purchases.data.mapper

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.purchases.data.local.entity.PurchaseEntity
import com.cajaclara.app.feature.purchases.data.local.entity.PurchaseLineEntity
import com.cajaclara.app.feature.purchases.domain.model.Purchase
import com.cajaclara.app.feature.purchases.domain.model.PurchaseLine
import com.cajaclara.app.feature.purchases.domain.valueobject.PurchaseId
import java.time.Instant

fun PurchaseEntity.toDomain(): Purchase = Purchase(
    id = PurchaseId(id),
    purchasedAt = Instant.ofEpochMilli(purchasedAt),
    totalInvestment = Money(totalInvestmentCents),
    note = note,
    createdAt = Instant.ofEpochMilli(createdAt),
)

fun Purchase.toEntity(): PurchaseEntity = PurchaseEntity(
    id = id.value,
    purchasedAt = purchasedAt.toEpochMilli(),
    totalInvestmentCents = totalInvestment.cents,
    note = note,
    createdAt = createdAt.toEpochMilli(),
)

fun PurchaseLineEntity.toDomain(): PurchaseLine = PurchaseLine(
    id = id,
    purchaseId = PurchaseId(purchaseId),
    productId = ProductId(productId),
    productNameSnapshot = productNameSnapshot,
    quantity = Quantity(quantity),
    unitCost = Money(unitCostCents),
)

fun PurchaseLine.toEntity(): PurchaseLineEntity = PurchaseLineEntity(
    id = id,
    purchaseId = purchaseId.value,
    productId = productId.value,
    productNameSnapshot = productNameSnapshot,
    quantity = quantity.value,
    unitCostCents = unitCost.cents,
)
