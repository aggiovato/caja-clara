package com.cajaclara.app.feature.stats.data.repository

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.products.data.local.dao.PriceHistoryDao
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.sales.data.local.dao.SaleDao
import com.cajaclara.app.feature.sales.data.mapper.toDomain
import com.cajaclara.app.feature.stats.domain.SalesAnalytics
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import com.cajaclara.app.feature.stats.domain.model.ProductPricePoint
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import com.cajaclara.app.core.money.Money
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/** Room-backed [AnalyticsRepository]: aggregates sales and price history into the stats models. */
class RoomAnalyticsRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val priceHistoryDao: PriceHistoryDao,
    private val zoneId: ZoneId,
) : AnalyticsRepository {

    override fun observeDailyBalance(date: LocalDate): Flow<DailyBalance> {
        val start = startMillis(date)
        val end = startMillis(date.plusDays(1))
        return combine(
            saleDao.observeBetween(start, end),
            saleDao.observeUnitsSoldBetween(start, end),
        ) { sales, units ->
            SalesAnalytics.dailyBalance(date, sales.map { it.toDomain() }, units)
        }
    }

    override fun observeSalesEvolution(range: DateRange): Flow<List<DailySalesPoint>> {
        val start = startMillis(range.start)
        val end = startMillis(range.end.plusDays(1))
        return saleDao.observeBetween(start, end).map { sales ->
            SalesAnalytics.salesEvolution(range, sales.map { it.toDomain() }, zoneId)
        }
    }

    override fun observeProductPriceEvolution(productId: ProductId): Flow<List<ProductPricePoint>> =
        priceHistoryDao.observeForProduct(productId.value).map { rows ->
            rows.map { row ->
                ProductPricePoint(
                    date = Instant.ofEpochMilli(row.createdAt).atZone(zoneId).toLocalDate(),
                    cost = Money(row.newCostCents),
                    pvp = Money(row.newPvpCents),
                )
            }
        }

    private fun startMillis(date: LocalDate): Long =
        date.atStartOfDay(zoneId).toInstant().toEpochMilli()
}
