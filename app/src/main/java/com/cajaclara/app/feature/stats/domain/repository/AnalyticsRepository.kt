package com.cajaclara.app.feature.stats.domain.repository

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.model.DailyCashPoint
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import com.cajaclara.app.feature.stats.domain.model.ProductPricePoint
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Port for analytics queries (section 14). Implemented by Room in the data layer
 * (`RoomAnalyticsRepository`). Aggregates sales and manual profit into balances and
 * the series shown on the stats screens.
 */
interface AnalyticsRepository {

    /** Observe the computed balance for a single [date]. */
    fun observeDailyBalance(date: LocalDate): Flow<DailyBalance>

    /** Observe the daily sales series (revenue/cost/profit) for an inclusive [range]. */
    fun observeSalesEvolution(range: DateRange): Flow<List<DailySalesPoint>>

    /** Observe the daily cash-flow series (sales in vs purchases out) for an inclusive [range]. */
    fun observeCashFlow(range: DateRange): Flow<List<DailyCashPoint>>

    /** Observe the business account balance: all sales revenue minus all purchase investment. */
    fun observeAccountBalance(): Flow<Money>

    /** Observe a product's cost/price evolution over time. */
    fun observeProductPriceEvolution(productId: ProductId): Flow<List<ProductPricePoint>>
}
