package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.valueobject.Margin
import com.cajaclara.app.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

/**
 * Thrown when a cost/PVP edit would leave the product's profit margin below the configured
 * minimum. [resultingPercent] is null when the margin can't be computed (e.g. price is zero).
 */
class MinMarginViolationException(
    val minPercent: Double,
    val resultingPercent: Double?,
) : IllegalStateException("Resulting margin ${resultingPercent ?: "n/a"}% is below the minimum $minPercent%")

/**
 * Enforces the configured minimum profit margin (over the sale price). No-op when no minimum is
 * set; otherwise throws [MinMarginViolationException] if [margin] falls below it.
 */
internal suspend fun SettingsRepository.requireMinMargin(margin: Margin) {
    val min = observe().first().minMarginPercent
    if (min <= 0.0) return
    val resulting = margin.percentOnPrice
    if (resulting == null || resulting < min) {
        throw MinMarginViolationException(min, resulting)
    }
}
