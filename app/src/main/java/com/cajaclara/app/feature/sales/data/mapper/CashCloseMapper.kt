package com.cajaclara.app.feature.sales.data.mapper

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.sales.data.local.entity.CashCloseEntity
import com.cajaclara.app.feature.sales.domain.model.CashClose
import java.time.Instant
import java.time.LocalDate

fun CashCloseEntity.toDomain(): CashClose = CashClose(
    date = LocalDate.ofEpochDay(epochDay),
    expectedRevenue = Money(expectedRevenueCents),
    expectedCost = Money(expectedCostCents),
    countedCash = Money(countedCashCents),
    salesCount = salesCount,
    closedAt = Instant.ofEpochMilli(closedAt),
    note = note,
)

fun CashClose.toEntity(): CashCloseEntity = CashCloseEntity(
    epochDay = date.toEpochDay(),
    expectedRevenueCents = expectedRevenue.cents,
    expectedCostCents = expectedCost.cents,
    countedCashCents = countedCash.cents,
    salesCount = salesCount,
    closedAt = closedAt.toEpochMilli(),
    note = note,
)
