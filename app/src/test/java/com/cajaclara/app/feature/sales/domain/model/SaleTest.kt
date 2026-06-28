package com.cajaclara.app.feature.sales.domain.model

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class SaleTest {

    @Test
    fun `total profit is revenue minus cost`() {
        val sale = Sale(
            id = SaleId.UNSAVED,
            soldAt = Instant.EPOCH,
            totalRevenue = Money.fromPesos("30,00"),
            totalCost = Money.fromPesos("12,00"),
            createdAt = Instant.EPOCH,
        )
        assertEquals(Money(1800), sale.totalProfit)
    }
}
