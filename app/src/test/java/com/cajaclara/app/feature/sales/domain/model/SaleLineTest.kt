package com.cajaclara.app.feature.sales.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import org.junit.Assert.assertEquals
import org.junit.Test

class SaleLineTest {

    private fun line(
        quantity: Quantity = Quantity(3),
        unitCost: Money = Money.fromPesos("4,00"),
        unitPvp: Money = Money.fromPesos("10,00"),
    ) = SaleLine(
        saleId = SaleId.UNSAVED,
        productId = ProductId(1),
        productNameSnapshot = "Coffee",
        quantity = quantity,
        unitCostSnapshot = unitCost,
        unitPvpSnapshot = unitPvp,
    )

    @Test
    fun `line totals are derived from snapshots and quantity`() {
        val l = line(quantity = Quantity(3))
        assertEquals(Money(3000), l.lineRevenue) // 10,00 * 3
        assertEquals(Money(1200), l.lineCost)    // 4,00 * 3
        assertEquals(Money(1800), l.lineProfit)  // revenue - cost
    }

    @Test
    fun `zero quantity gives zero totals`() {
        val l = line(quantity = Quantity.ZERO)
        assertEquals(Money.ZERO, l.lineRevenue)
        assertEquals(Money.ZERO, l.lineCost)
        assertEquals(Money.ZERO, l.lineProfit)
    }
}
