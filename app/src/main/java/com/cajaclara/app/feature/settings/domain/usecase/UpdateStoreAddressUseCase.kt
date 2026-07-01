package com.cajaclara.app.feature.settings.domain.usecase

import com.cajaclara.app.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

/**
 * Updates the shop's physical address, shown on the shareable product images. The value is
 * trimmed; a blank address clears it (means "none configured").
 */
class UpdateStoreAddressUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(storeAddress: String) {
        val current = repository.observe().first()
        repository.update(current.copy(storeAddress = storeAddress.trim()))
    }
}
