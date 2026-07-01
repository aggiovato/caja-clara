package com.cajaclara.app.feature.settings.domain.repository

import com.cajaclara.app.feature.settings.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

/** Port for the app's global settings. Backed by a single Room row. */
interface SettingsRepository {

    /** Observe the current settings (defaults until the user changes them). */
    fun observe(): Flow<AppSettings>

    /** Persist the settings. */
    suspend fun update(settings: AppSettings)
}
