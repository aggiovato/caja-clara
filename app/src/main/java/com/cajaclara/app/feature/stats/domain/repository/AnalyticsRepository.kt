package com.cajaclara.app.feature.stats.domain.repository

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.model.ProductPricePoint
import com.cajaclara.app.feature.stats.domain.model.ProfitPoint
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

    /** Observe the profit-over-time series for an inclusive [range]. */
    fun observeProfitEvolution(range: DateRange): Flow<List<ProfitPoint>>

    /** Observe a product's cost/price evolution over time. */
    fun observeProductPriceEvolution(productId: ProductId): Flow<List<ProductPricePoint>>
}
