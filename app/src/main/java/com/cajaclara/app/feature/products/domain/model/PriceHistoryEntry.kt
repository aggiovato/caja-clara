package com.cajaclara.app.feature.products.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Instant

/**
 * Records a cost/price change for a product (section 11.3). Written only when a value
 * actually changes; feeds price-evolution charts and a simple audit trail.
 */
data class PriceHistoryEntry(
    val id: Long = 0L,
    val productId: ProductId,
    val oldCost: Money?,
    val newCost: Money,
    val oldPvp: Money?,
    val newPvp: Money,
    val reason: String? = null,
    val createdAt: Instant,
)
