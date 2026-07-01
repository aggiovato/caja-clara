package com.cajaclara.app.feature.settings.domain.usecase

import com.cajaclara.app.feature.settings.domain.model.AppSettings
import com.cajaclara.app.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

/** Observes the app's global settings. */
class ObserveSettingsUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppSettings> = repository.observe()
}
