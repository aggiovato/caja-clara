package com.cajaclara.app.feature.stats.domain.usecase

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.stats.domain.model.DailyCashPoint
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow

/** Observes the daily cash-flow series (sales in vs purchases out) across an inclusive [range]. */
class ObserveCashFlowUseCase(
    private val repository: AnalyticsRepository,
) {
    operator fun invoke(range: DateRange): Flow<List<DailyCashPoint>> =
        repository.observeCashFlow(range)
}
