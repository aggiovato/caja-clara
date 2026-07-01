package com.cajaclara.app.ui.settings.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.cajaclara.app.feature.settings.domain.usecase.UpdateMinMarginUseCase
import com.cajaclara.app.feature.settings.domain.usecase.UpdateStoreAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** State for the settings screen. */
data class SettingsUiState(
    val minMarginPercent: Double = 0.0,
    val storeAddress: String = "",
    val isLoading: Boolean = true,
    val savedMessage: String? = null,
    val error: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    private val updateMinMargin: UpdateMinMarginUseCase,
    private val updateStoreAddress: UpdateStoreAddressUseCase,
) : ViewModel() {

    private val transient = MutableStateFlow(TransientState())

    val state: StateFlow<SettingsUiState> =
        combine(observeSettings(), transient) { settings, t ->
            SettingsUiState(
                minMarginPercent = settings.minMarginPercent,
                storeAddress = settings.storeAddress,
                isLoading = false,
                savedMessage = t.savedMessage,
                error = t.error,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun saveMinMargin(text: String) {
        val percent = text.trim().replace(',', '.').toDoubleOrNull()
        if (percent == null || percent < 0.0 || percent > 99.0) {
            transient.update { it.copy(error = "Introduce un porcentaje entre 0 y 99") }
            return
        }
        viewModelScope.launch {
            runCatching { updateMinMargin(percent) }
                .onSuccess { transient.update { it.copy(savedMessage = "Guardado", error = null) } }
                .onFailure { e -> transient.update { it.copy(error = e.message ?: "No se pudo guardar") } }
        }
    }

    fun saveStoreAddress(text: String) {
        viewModelScope.launch {
            runCatching { updateStoreAddress(text) }
                .onSuccess { transient.update { it.copy(savedMessage = "Guardado", error = null) } }
                .onFailure { e -> transient.update { it.copy(error = e.message ?: "No se pudo guardar") } }
        }
    }

    fun onMessageShown() = transient.update { it.copy(savedMessage = null, error = null) }

    private data class TransientState(val savedMessage: String? = null, val error: String? = null)
}
