package com.cajaclara.app.feature.stats.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow

/** Observes the business account balance (all sales revenue minus all purchase investment). */
class ObserveAccountBalanceUseCase(
    private val repository: AnalyticsRepository,
) {
    operator fun invoke(): Flow<Money> = repository.observeAccountBalance()
}
