package com.cajaclara.app.feature.settings.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row settings table (always id = 1). */
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val minMarginPercent: Double,
    val storeAddress: String = "",
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
