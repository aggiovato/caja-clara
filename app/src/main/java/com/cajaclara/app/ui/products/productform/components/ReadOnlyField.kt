package com.cajaclara.app.ui.products.productform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.designsystem.moneyAnnotatedString
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppBorderWidth
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/**
 * A read-only labeled value. Styled as disabled (muted fill + soft gray outline) so it does
 * not look selected/editable; the edit (✏️) action is the only interactive accent.
 */
@Composable
internal fun ReadOnlyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
) {
    Column(modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppCornerRadius),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(AppBorderWidth, MaterialTheme.colorScheme.outline),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            ) {
                Text(
                    text = moneyAnnotatedString(value),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                if (onEdit != null) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar $label",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).clickable(onClick = onEdit),
                    )
                }
            }
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun ReadOnlyFieldPreview() {
    CajaClaraTheme {
        ReadOnlyField("Coste", "2,10 CUP", Modifier.padding(16.dp), onEdit = {})
    }
}
