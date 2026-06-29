package com.cajaclara.app.feature.stats.domain.usecase

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow

/** Observes the daily sales series (revenue/cost/profit) across an inclusive [range]. */
class ObserveSalesEvolutionUseCase(
    private val repository: AnalyticsRepository,
) {
    operator fun invoke(range: DateRange): Flow<List<DailySalesPoint>> =
        repository.observeSalesEvolution(range)
}
