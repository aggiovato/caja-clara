package com.cajaclara.app.ui.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** App confirmation dialog. Centralized here so its style can be customized in one place. */
@Composable
fun AppConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Eliminar",
    dismissText: String = "Cancelar",
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(AppCornerRadius),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(dismissText) }
        },
    )
}

@LightPreview
@DarkPreview
@Composable
private fun AppConfirmDialogPreview() {
    CajaClaraTheme {
        AppConfirmDialog(
            title = "Eliminar imagen",
            message = "¿Quieres quitar la imagen de este producto?",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
