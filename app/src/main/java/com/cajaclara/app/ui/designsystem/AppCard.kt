package com.cajaclara.app.ui.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppBorderWidth
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/**
 * The standard app card.
 * - Light: white surface with just a soft gray outline of [AppBorderWidth] (no shadow).
 * - Dark: no outline; a shadow lifts the card off the dark background.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(AppCornerRadius)
    val darkShadow = MaterialTheme.colorScheme.background

    val cardModifier = if (dark) {
        modifier.shadow(elevation = 2.dp, shape = shape, ambientColor = darkShadow, spotColor = darkShadow, clip = false)
    } else {
        modifier
    }
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val border = if (dark) null else BorderStroke(AppBorderWidth, MaterialTheme.colorScheme.outline)

    if (onClick != null) {
        Card(onClick = onClick, modifier = cardModifier, shape = shape, colors = colors, elevation = elevation, border = border, content = content)
    } else {
        Card(modifier = cardModifier, shape = shape, colors = colors, elevation = elevation, border = border, content = content)
    }
}

@LightPreview
@DarkPreview
@Composable
private fun AppCardPreview() {
    CajaClaraTheme {
        AppCard(Modifier.padding(16.dp)) {
            Text("Contenido de la card", Modifier.padding(16.dp))
        }
    }
}
