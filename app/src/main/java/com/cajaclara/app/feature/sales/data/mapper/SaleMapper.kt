package com.cajaclara.app.feature.sales.data.mapper

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.sales.data.local.entity.SaleEntity
import com.cajaclara.app.feature.sales.data.local.entity.SaleLineEntity
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.model.SaleLine
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import java.time.Instant

fun SaleEntity.toDomain(): Sale = Sale(
    id = SaleId(id),
    soldAt = Instant.ofEpochMilli(soldAt),
    totalRevenue = Money(totalRevenueCents),
    totalCost = Money(totalCostCents),
    note = note,
    createdAt = Instant.ofEpochMilli(createdAt),
)

fun Sale.toEntity(): SaleEntity = SaleEntity(
    id = id.value,
    soldAt = soldAt.toEpochMilli(),
    totalRevenueCents = totalRevenue.cents,
    totalCostCents = totalCost.cents,
    note = note,
    createdAt = createdAt.toEpochMilli(),
)

fun SaleLineEntity.toDomain(): SaleLine = SaleLine(
    id = id,
    saleId = SaleId(saleId),
    productId = ProductId(productId),
    productNameSnapshot = productNameSnapshot,
    quantity = Quantity(quantity),
    unitCostSnapshot = Money(unitCostCents),
    unitPvpSnapshot = Money(unitPvpCents),
)

fun SaleLine.toEntity(): SaleLineEntity = SaleLineEntity(
    id = id,
    saleId = saleId.value,
    productId = productId.value,
    productNameSnapshot = productNameSnapshot,
    quantity = quantity.value,
    unitCostCents = unitCostSnapshot.cents,
    unitPvpCents = unitPvpSnapshot.cents,
)
