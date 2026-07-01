package com.cajaclara.app.ui.home.home

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.stats.domain.model.DailyBalance

/** State for the home dashboard: business account, today's figures and stock alerts. */
data class HomeUiState(
    val accountBalance: Money = Money.ZERO,
    val today: DailyBalance? = null,
    val isCashClosed: Boolean = false,
    val soldOutCount: Int = 0,
    val lowStockCount: Int = 0,
    val isLoading: Boolean = true,
) {
    val hasStockAlerts: Boolean = soldOutCount > 0 || lowStockCount > 0
}
