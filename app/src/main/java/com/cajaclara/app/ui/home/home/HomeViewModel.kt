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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeAccountBalance: ObserveAccountBalanceUseCase,
    observeDailyBalance: ObserveDailyBalanceUseCase,
    observeCashClose: ObserveCashCloseUseCase,
    observeProducts: ObserveProductsUseCase,
    observeInsights: ObserveBusinessInsightsUseCase,
    clock: Clock,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now(clock)

    val state: StateFlow<HomeUiState> =
        combine(
            observeAccountBalance(),
            observeDailyBalance(today),
            observeCashClose(today),
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
    }
}
