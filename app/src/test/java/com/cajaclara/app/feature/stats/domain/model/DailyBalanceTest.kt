package com.cajaclara.app.feature.stats.domain.model

import com.cajaclara.app.core.money.Money
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DailyBalanceTest {

    private fun balance(
        salesRevenue: Money = Money.fromPesos("100,00"),
        salesCost: Money = Money.fromPesos("60,00"),
        manualProfit: Money = Money.fromPesos("15,00"),
    ) = DailyBalance(
        date = LocalDate.of(2026, 6, 28),
        salesRevenue = salesRevenue,
        salesCost = salesCost,
        manualProfit = manualProfit,
        salesCount = 17,
        productsSoldCount = 23,
    )

    @Test
    fun `sales profit is revenue minus cost`() {
        assertEquals(Money(4000), balance().salesProfit) // 100,00 - 60,00
    }

    @Test
    fun `total profit adds sales profit and manual profit`() {
        assertEquals(Money(5500), balance().totalProfit) // 40,00 + 15,00
    }

    @Test
    fun `total profit equals sales profit when there is no manual entry`() {
        val b = balance(manualProfit = Money.ZERO)
        assertEquals(b.salesProfit, b.totalProfit)
    }
}
