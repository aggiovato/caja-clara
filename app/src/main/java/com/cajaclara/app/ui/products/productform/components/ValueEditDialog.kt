package com.cajaclara.app.ui.products.productform.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.cajaclara.app.ui.designsystem.AppTextField
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/**
 * Dialog to enter a single value (cost/PVP/stock). Confirming runs the dedicated action,
 * which writes the corresponding history (price history or stock movement).
 */
@Composable
internal fun ValueEditDialog(
    title: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Decimal,
) {
    val field = rememberTextFieldState()
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(AppCornerRadius),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title) },
        text = {
            AppTextField(field, placeholder = placeholder, keyboardType = keyboardType)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(field.text.toString()) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

@LightPreview
@DarkPreview
@Composable
private fun ValueEditDialogPreview() {
    CajaClaraTheme {
        ValueEditDialog("Cambiar coste", onConfirm = {}, onDismiss = {}, placeholder = "0,00")
    }
}
