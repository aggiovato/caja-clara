package com.cajaclara.app.ui.stats.stats

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint

/** State for the stats screen: today's balance and the daily sales series for the chosen range. */
data class StatsUiState(
    val dailyBalance: DailyBalance? = null,
    val rangeDays: Int = 7,
    val salesPoints: List<DailySalesPoint> = emptyList(),
    val isLoading: Boolean = true,
) {
    /** Total profit across the selected range. */
    val rangeProfit: Money = salesPoints.fold(Money.ZERO) { acc, p -> acc + p.profit }
}
