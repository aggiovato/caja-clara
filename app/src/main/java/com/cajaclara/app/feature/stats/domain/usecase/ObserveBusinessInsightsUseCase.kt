package com.cajaclara.app.feature.stats.domain.usecase

import com.cajaclara.app.feature.stats.domain.model.BusinessInsights
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow

/** Observes business health insights (profitability and top products). */
class ObserveBusinessInsightsUseCase(
    private val repository: AnalyticsRepository,
) {
    operator fun invoke(): Flow<BusinessInsights> = repository.observeBusinessInsights()
}
