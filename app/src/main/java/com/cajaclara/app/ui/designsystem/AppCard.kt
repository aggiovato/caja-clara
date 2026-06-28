package com.cajaclara.app.ui.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.theme.AppBorderWidth
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/**
 * The standard app card: white surface, primary outline of [AppBorderWidth], soft shadow
 * and the shared [AppCornerRadius]. Reuse this so every card across screens looks identical.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(AppBorderWidth, MaterialTheme.colorScheme.primary),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppCardPreview() {
    CajaClaraTheme {
        AppCard(Modifier.padding(16.dp)) {
            Text("Contenido de la card", Modifier.padding(16.dp))
        }
    }
}
