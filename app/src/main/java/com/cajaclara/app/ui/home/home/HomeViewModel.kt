package com.cajaclara.app.ui.home.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import com.cajaclara.app.feature.sales.domain.usecase.ObserveCashCloseUseCase
import com.cajaclara.app.feature.stats.domain.usecase.ObserveAccountBalanceUseCase
import com.cajaclara.app.feature.stats.domain.usecase.ObserveBusinessInsightsUseCase
import com.cajaclara.app.feature.stats.domain.usecase.ObserveDailyBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeAccountBalance: ObserveAccountBalanceUseCase,
    observeDailyBalance: ObserveDailyBalanceUseCase,
    observeCashClose: ObserveCashCloseUseCase,
    observeProducts: ObserveProductsUseCase,
    observeInsights: ObserveBusinessInsightsUseCase,
    clock: Clock,
) : ViewModel() {

    // Re-read the day periodically so "today's" figures roll over at midnight instead of being
    // frozen to the day the dashboard was opened.
    private val today: Flow<LocalDate> = flow {
        while (true) {
            emit(LocalDate.now(clock))
            delay(DAY_REFRESH_INTERVAL_MS.milliseconds)
        }
    }.distinctUntilChanged()

    val state: StateFlow<HomeUiState> =
        combine(
            observeAccountBalance(),
            today.flatMapLatest { observeDailyBalance(it) },
            today.flatMapLatest { observeCashClose(it) },
            observeProducts(ProductFilter.ALL),
            observeInsights(),
        ) { balance, daily, close, products, insights ->
            HomeUiState(
                accountBalance = balance,
                today = daily,
                isCashClosed = close != null,
                soldOutCount = products.count { it.status == ProductStatus.SOLD_OUT },
                lowStockCount = products.count {
                    it.status == ProductStatus.ACTIVE && it.stockQuantity.value in 1..LOW_STOCK_THRESHOLD
                },
                insights = insights,
                isLoading = false,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    companion object {
        /** Stock at or below this (and above zero) counts as "low" until it's configurable. */
        const val LOW_STOCK_THRESHOLD = 5

        /** How often to re-check the current date so the dashboard rolls over at midnight. */
        private const val DAY_REFRESH_INTERVAL_MS = 60_000L
    }
}
