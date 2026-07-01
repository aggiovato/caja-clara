package com.cajaclara.app.feature.settings.data.repository

import com.cajaclara.app.feature.settings.data.local.dao.SettingsDao
import com.cajaclara.app.feature.settings.data.local.entity.SettingsEntity
import com.cajaclara.app.feature.settings.domain.model.AppSettings
import com.cajaclara.app.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room-backed [SettingsRepository]. A missing row means defaults. */
class RoomSettingsRepository @Inject constructor(
    private val dao: SettingsDao,
) : SettingsRepository {

    override fun observe(): Flow<AppSettings> =
        dao.observe().map { row ->
            row?.let {
                AppSettings(minMarginPercent = it.minMarginPercent, storeAddress = it.storeAddress)
            } ?: AppSettings.DEFAULT
        }

    override suspend fun update(settings: AppSettings) {
        dao.upsert(
            SettingsEntity(
                minMarginPercent = settings.minMarginPercent,
                storeAddress = settings.storeAddress,
            ),
        )
    }
}
