package com.cajaclara.app.feature.stats.domain.model

import com.cajaclara.app.core.money.Money
import java.time.LocalDate

/** A single point in the profit-over-time series (one day's profit). */
data class ProfitPoint(
    val date: LocalDate,
    val profit: Money,
)
