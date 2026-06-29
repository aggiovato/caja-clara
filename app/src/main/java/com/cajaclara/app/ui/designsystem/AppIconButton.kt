package com.cajaclara.app.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/**
 * Solid icon button with the app's token radius. [subtle] uses a neutral surface tone
 * instead of primary (for secondary actions like pause/archive).
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtle: Boolean = false,
) {
    val shape = RoundedCornerShape(AppCornerRadius)
    val colors = if (subtle) {
        // A slightly raised neutral so the button lifts off the page background.
        IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    }
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = colors,
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@LightPreview
@DarkPreview
@Composable
private fun AppIconButtonPreview() {
    CajaClaraTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppIconButton(Icons.Filled.PhotoLibrary, "Galería", {})
            AppIconButton(Icons.Filled.PhotoCamera, "Cámara", {})
        }
    }
}
