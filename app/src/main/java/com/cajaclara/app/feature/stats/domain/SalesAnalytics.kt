package com.cajaclara.app.feature.stats.domain

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.purchases.domain.model.Purchase
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.model.DailyCashPoint
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import java.time.LocalDate
import java.time.ZoneId

/**
 * Pure aggregation logic for analytics, kept framework-free so it is unit-testable without
 * Room. The repository fetches rows and delegates the math here.
 */
object SalesAnalytics {

    /** Build a [DailyBalance] for [date] from its sales and total units sold. Manual profit is 0. */
    fun dailyBalance(date: LocalDate, sales: List<Sale>, unitsSold: Int): DailyBalance =
        DailyBalance(
            date = date,
            salesRevenue = sales.fold(Money.ZERO) { acc, s -> acc + s.totalRevenue },
            salesCost = sales.fold(Money.ZERO) { acc, s -> acc + s.totalCost },
            manualProfit = Money.ZERO,
            salesCount = sales.size,
            productsSoldCount = unitsSold,
        )

    /**
     * Daily sales series across [range] (inclusive). Sales are grouped by their local day in
     * [zone]; days with no sales appear with zero figures so the series is continuous.
     */
    fun salesEvolution(range: DateRange, sales: List<Sale>, zone: ZoneId): List<DailySalesPoint> {
        val byDay = sales.groupBy { it.soldAt.atZone(zone).toLocalDate() }
        return generateSequence(range.start) { day ->
            day.plusDays(1).takeIf { !it.isAfter(range.end) }
        }.map { day ->
            val daySales = byDay[day].orEmpty()
            DailySalesPoint(
                date = day,
                revenue = daySales.fold(Money.ZERO) { acc, s -> acc + s.totalRevenue },
                cost = daySales.fold(Money.ZERO) { acc, s -> acc + s.totalCost },
            )
        }.toList()
    }

    /**
     * Daily cash-flow series across [range] (inclusive): sales revenue in vs purchase investment
     * out per local day in [zone]. Days with no activity appear with zero figures.
     */
    fun cashFlow(range: DateRange, sales: List<Sale>, purchases: List<Purchase>, zone: ZoneId): List<DailyCashPoint> {
        val salesByDay = sales.groupBy { it.soldAt.atZone(zone).toLocalDate() }
        val purchasesByDay = purchases.groupBy { it.purchasedAt.atZone(zone).toLocalDate() }
        return generateSequence(range.start) { day ->
            day.plusDays(1).takeIf { !it.isAfter(range.end) }
        }.map { day ->
            DailyCashPoint(
                date = day,
                salesRevenue = salesByDay[day].orEmpty().fold(Money.ZERO) { acc, s -> acc + s.totalRevenue },
                purchaseInvestment = purchasesByDay[day].orEmpty().fold(Money.ZERO) { acc, p -> acc + p.totalInvestment },
            )
        }.toList()
    }
}
