package com.cajaclara.app.feature.products.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ProductTest {

    private fun product(
        cost: Money = Money.fromPesos("4,00"),
        price: Money = Money.fromPesos("10,00"),
        stock: Quantity = Quantity(5),
        status: ProductStatus = ProductStatus.ACTIVE,
    ) = Product(
        id = ProductId.UNSAVED,
        name = "Coffee",
        currentCost = cost,
        currentPvp = price,
        stockQuantity = stock,
        status = status,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
    )

    @Test
    fun `margin is derived from current cost and price`() {
        val p = product(cost = Money.fromPesos("4,00"), price = Money.fromPesos("10,00"))
        assertEquals(Money(600), p.margin.unitMargin)
        assertEquals(60.0, p.margin.percentOnPrice!!, 0.0001)
    }

    @Test
    fun `is sellable only when active`() {
        assertTrue(product(status = ProductStatus.ACTIVE).isSellable)
        assertFalse(product(status = ProductStatus.SOLD_OUT).isSellable)
        assertFalse(product(status = ProductStatus.PAUSED).isSellable)
    }
}
