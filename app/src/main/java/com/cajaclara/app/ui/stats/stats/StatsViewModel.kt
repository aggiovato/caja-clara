package com.cajaclara.app.ui.stats.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.stats.domain.usecase.ObserveDailyBalanceUseCase
import com.cajaclara.app.feature.stats.domain.usecase.ObserveSalesEvolutionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatsViewModel @Inject constructor(
    observeDailyBalance: ObserveDailyBalanceUseCase,
    observeSalesEvolution: ObserveSalesEvolutionUseCase,
    clock: Clock,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now(clock)
    private val rangeDays = MutableStateFlow(7)

    private val series = rangeDays.flatMapLatest { days ->
        observeSalesEvolution(DateRange(today.minusDays((days - 1).toLong()), today))
    }

    val state: StateFlow<StatsUiState> =
        combine(observeDailyBalance(today), rangeDays, series) { balance, days, points ->
            StatsUiState(dailyBalance = balance, rangeDays = days, salesPoints = points, isLoading = false)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())

    fun onRangeSelected(days: Int) {
        rangeDays.value = days
    }
}
