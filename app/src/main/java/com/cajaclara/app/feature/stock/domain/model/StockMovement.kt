package com.cajaclara.app.feature.stock.domain.model

import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import java.time.Instant

/**
 * Records a change to a product's stock (section 11.4). The [type] explains why the
 * on-hand quantity changed; [quantity] is the magnitude of the movement.
 */
data class StockMovement(
    val id: Long = 0L,
    val productId: ProductId,
    val type: StockMovementType,
    val quantity: Quantity,
    val note: String? = null,
    val createdAt: Instant,
)
