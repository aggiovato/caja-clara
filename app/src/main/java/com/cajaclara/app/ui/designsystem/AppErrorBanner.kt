package com.cajaclara.app.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** A highlighted inline error banner (icon + message) for form/validation errors. */
@Composable
fun AppErrorBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(AppCornerRadius),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun AppErrorBannerPreview() {
    CajaClaraTheme {
        AppErrorBanner("El margen (mín. 10%) no se cumple con este coste y PVP", Modifier.padding(16.dp))
    }
}
