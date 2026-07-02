package com.cajaclara.app.ui.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** Snackbar payload that also carries whether it's an error (red) or a neutral/success message. */
class AppSnackbarVisuals(
    override val message: String,
    val isError: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
) : SnackbarVisuals

/** Show an app-styled, auto-dismissing message. [isError] switches to the error palette. */
suspend fun SnackbarHostState.showMessage(message: String, isError: Boolean = false) {
    showSnackbar(AppSnackbarVisuals(message = message, isError = isError))
}

/**
 * App-styled snackbar host. Place it near the **top** of the screen (the action buttons live at
 * the bottom), e.g. `Modifier.align(Alignment.TopCenter)`. Each message renders as a rounded,
 * tinted card with a couple of faint decorative circles that echo the app background.
 */
@Composable
fun AppSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState, modifier) { data ->
        val isError = (data.visuals as? AppSnackbarVisuals)?.isError ?: false
        AppSnackbarContent(message = data.visuals.message, isError = isError)
    }
}

@Composable
private fun AppSnackbarContent(message: String, isError: Boolean) {
    val container =
        if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
    val content =
        if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer

    Surface(
        shape = RoundedCornerShape(AppCornerRadius),
        color = container,
        contentColor = content,
        shadowElevation = 6.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentWidth(),
    ) {
        Box {
            // Faint decorative circles clipped to the card, in the spirit of AppBackground.
            Canvas(Modifier.matchParentSize()) {
                val tint = content.copy(alpha = 0.08f)
                drawCircle(tint, radius = size.height * 0.85f, center = Offset(size.width * 0.94f, size.height * 0.0f))
                drawCircle(
                    tint,
                    radius = size.height * 0.5f,
                    center = Offset(size.width * 0.06f, size.height),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = if (isError) Icons.Outlined.ErrorOutline else Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text(message, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun AppSnackbarPreview() {
    CajaClaraTheme {
        Box(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Column {
                AppSnackbarContent(message = "Venta registrada", isError = false)
                AppSnackbarContent(message = "No se pudo registrar la venta", isError = true)
            }
        }
    }
}
