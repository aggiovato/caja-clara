package com.cajaclara.app.feature.stats.domain.usecase

import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Observes the computed profit balance for a single [date]. */
class ObserveDailyBalanceUseCase(
    private val repository: AnalyticsRepository,
) {
    operator fun invoke(date: LocalDate): Flow<DailyBalance> = repository.observeDailyBalance(date)
}
