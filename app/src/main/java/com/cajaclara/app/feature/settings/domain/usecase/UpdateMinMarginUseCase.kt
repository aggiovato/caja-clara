package com.cajaclara.app.feature.settings.domain.usecase

import com.cajaclara.app.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

/**
 * Updates the minimum profit margin (percent over the sale price). Validates the range; the
 * change applies to future cost/PVP edits, never retroactively to existing products.
 */
class UpdateMinMarginUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(minMarginPercent: Double) {
        require(minMarginPercent in 0.0..99.0) { "Minimum margin must be between 0 and 99%" }
        val current = repository.observe().first()
        repository.update(current.copy(minMarginPercent = minMarginPercent))
    }
}
