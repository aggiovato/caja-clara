package com.cajaclara.app.feature.stats.domain.model

import com.cajaclara.app.core.money.Money
import java.time.LocalDate

/** A single point in a product's cost/price evolution series. */
data class ProductPricePoint(
    val date: LocalDate,
    val cost: Money,
    val pvp: Money,
)
