package com.cajaclara.app.feature.purchases.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.purchases.domain.valueobject.PurchaseId
import java.time.Instant

/**
 * A registered purchase (restock/investment): money spent buying stock. Mirrors a sale but is
 * cash going out. [totalInvestment] is the denormalized sum of its lines, never edited.
 * Purchases affect the business cash balance, not the daily sales profit.
 */
data class Purchase(
    val id: PurchaseId,
    val purchasedAt: Instant,
    val totalInvestment: Money,
    val note: String? = null,
    val createdAt: Instant,
)
