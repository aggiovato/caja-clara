package com.cajaclara.app.ui.settings.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cajaclara.app.ui.designsystem.AppCard
import com.cajaclara.app.ui.designsystem.AppPrimaryButton
import com.cajaclara.app.ui.designsystem.AppTextField
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.CajaClaraTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val marginField = rememberTextFieldState()
    val addressField = rememberTextFieldState()
    var prefilled by remember { mutableStateOf(false) }
    // Prefill once with the stored values when settings finish loading.
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && !prefilled) {
            marginField.setTextAndPlaceCursorAtEnd(formatPercent(state.minMarginPercent))
            addressField.setTextAndPlaceCursorAtEnd(state.storeAddress)
            prefilled = true
        }
    }
    SettingsScreen(
        state = state,
        marginField = marginField,
        addressField = addressField,
        onSaveMargin = { viewModel.saveMinMargin(marginField.text.toString()) },
        onSaveAddress = { viewModel.saveStoreAddress(addressField.text.toString()) },
        onMessageShown = viewModel::onMessageShown,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    marginField: TextFieldState,
    addressField: TextFieldState,
    onSaveMargin: () -> Unit,
    onSaveAddress: () -> Unit,
    onMessageShown: () -> Unit,
    onBack: () -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.savedMessage, state.error) {
        (state.error ?: state.savedMessage)?.let { snackbar.showSnackbar(it); onMessageShown() }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Margen mínimo de ganancia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Porcentaje mínimo de ganancia (sobre el PVP) que debe mantener cada producto. " +
                            "Al editar el coste o el PVP, se rechaza el cambio si el margen queda por debajo. " +
                            "0 = sin restricción.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    AppTextField(
                        marginField,
                        label = "Margen mínimo (%)",
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                    )
                    AppPrimaryButton(text = "Guardar", onClick = onSaveMargin, modifier = Modifier.fillMaxWidth())
                }
            }

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Dirección de la tienda", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Dirección física de tu tienda. Aparece al pie de las imágenes de producto " +
                            "que compartes por WhatsApp. Déjala vacía para no mostrarla.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    AppTextField(
                        addressField,
                        label = "Dirección",
                        placeholder = "Calle 23 #456, La Habana",
                        singleLine = false,
                        minLines = 2,
                    )
                    AppPrimaryButton(text = "Guardar", onClick = onSaveAddress, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

/** Format a percent without trailing zeros: 40.0 -> "40", 12.5 -> "12,5". */
private fun formatPercent(value: Double): String =
    (if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()).replace('.', ',')

@LightPreview
@DarkPreview
@Composable
private fun SettingsScreenPreview() {
    CajaClaraTheme {
        SettingsScreen(
            state = SettingsUiState(
                minMarginPercent = 40.0,
                storeAddress = "Calle 23 #456, La Habana",
                isLoading = false,
            ),
            marginField = rememberTextFieldState("40"),
            addressField = rememberTextFieldState("Calle 23 #456, La Habana"),
            onSaveMargin = {},
            onSaveAddress = {},
            onMessageShown = {},
            onBack = {},
        )
    }
}
