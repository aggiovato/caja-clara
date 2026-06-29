package com.cajaclara.app.feature.stats.domain

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class SalesAnalyticsTest {

    private val zone = ZoneOffset.UTC

    private fun sale(day: String, revenue: String, cost: String) = Sale(
        id = SaleId(day.hashCode().toLong()),
        soldAt = Instant.parse("${day}T12:00:00Z"),
        totalRevenue = Money.fromPesos(revenue),
        totalCost = Money.fromPesos(cost),
        createdAt = Instant.parse("${day}T12:00:00Z"),
    )

    @Test
    fun `dailyBalance sums revenue, cost and counts`() {
        val sales = listOf(sale("2026-06-29", "20,00", "8,00"), sale("2026-06-29", "5,00", "2,00"))
        val balance = SalesAnalytics.dailyBalance(LocalDate.of(2026, 6, 29), sales, unitsSold = 7)

        assertEquals(Money.fromPesos("25,00"), balance.salesRevenue)
        assertEquals(Money.fromPesos("10,00"), balance.salesCost)
        assertEquals(Money.fromPesos("15,00"), balance.salesProfit)
        assertEquals(Money.ZERO, balance.manualProfit)
        assertEquals(2, balance.salesCount)
        assertEquals(7, balance.productsSoldCount)
    }

    @Test
    fun `salesEvolution fills every day in range, zero when no sales`() {
        val range = DateRange(LocalDate.of(2026, 6, 27), LocalDate.of(2026, 6, 29))
        val sales = listOf(
            sale("2026-06-27", "10,00", "4,00"), // profit 6
            sale("2026-06-29", "30,00", "10,00"), // profit 20
        )
        val points = SalesAnalytics.salesEvolution(range, sales, zone)

        assertEquals(3, points.size)
        assertEquals(LocalDate.of(2026, 6, 27), points[0].date)
        assertEquals(Money.fromPesos("10,00"), points[0].revenue)
        assertEquals(Money.fromPesos("4,00"), points[0].cost)
        assertEquals(Money.fromPesos("6,00"), points[0].profit)
        assertEquals(Money.ZERO, points[1].revenue) // 28th, no sales
        assertEquals(Money.ZERO, points[1].profit)
        assertEquals(Money.fromPesos("20,00"), points[2].profit)
    }

    @Test
    fun `salesEvolution groups multiple sales in the same day`() {
        val range = DateRange.singleDay(LocalDate.of(2026, 6, 29))
        val sales = listOf(sale("2026-06-29", "10,00", "3,00"), sale("2026-06-29", "5,00", "1,00"))
        val points = SalesAnalytics.salesEvolution(range, sales, zone)

        assertEquals(1, points.size)
        assertEquals(Money.fromPesos("15,00"), points[0].revenue) // 10+5
        assertEquals(Money.fromPesos("4,00"), points[0].cost) // 3+1
        assertEquals(Money.fromPesos("11,00"), points[0].profit)
    }
}
