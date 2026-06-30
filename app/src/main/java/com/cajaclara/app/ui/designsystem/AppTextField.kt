package com.cajaclara.app.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppBorderWidth
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme
import kotlinx.coroutines.launch

/**
 * The app's text input: a bordered field sharing the card chrome. Uses [TextFieldState] so
 * text/cursor are local and synchronous. [AppSearchField] is this in "search mode".
 *
 * @param tooltip optional explanation shown via an info icon next to the label.
 * @param singleLine false + [minLines] turns it into a text area.
 */
@Composable
fun AppTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    tooltip: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLength: Int? = null,
) {
    Column(modifier) {
        if (label != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (tooltip != null) InfoTooltip(tooltip)
            }
        }
        var focused by remember { mutableStateOf(false) }
        // Primary border only while focused; idle uses a calm gray (the "Pausado" text tone),
        // readable in both light and dark without shouting.
        val borderColor by animateColorAsState(
            targetValue = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            label = "borderColor",
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppCornerRadius),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(AppBorderWidth, borderColor),
        ) {
            Row(
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                modifier = Modifier.padding(horizontal = 14.dp),
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                BasicTextField(
                    state = state,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focused = it.isFocused }
                        .padding(start = if (leadingIcon != null) 12.dp else 0.dp, top = 16.dp, bottom = 16.dp),
                    inputTransformation = maxLength?.let { InputTransformation.maxLength(it) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    lineLimits = if (singleLine) {
                        TextFieldLineLimits.SingleLine
                    } else {
                        TextFieldLineLimits.MultiLine(minHeightInLines = minLines, maxHeightInLines = Int.MAX_VALUE)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorator = { innerTextField ->
                        if (state.text.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        innerTextField()
                    },
                )
            }
        }
    }
}

/** Small info icon that shows [text] in a tooltip when tapped. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoTooltip(text: String) {
    val scope = rememberCoroutineScope()
    val state = rememberTooltipState(isPersistent = true)
    TooltipBox(
        // Rich tooltip using the standard inverse surface (dark on light, light on dark), so
        // it matches the plain tooltips and stands out from the content in both themes.
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            RichTooltip(
                shape = RoundedCornerShape(AppCornerRadius),
                colors = TooltipDefaults.richTooltipColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                ),
            ) { Text(text) }
        },
        state = state,
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Más información",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(18.dp)
                .clickable { scope.launch { state.show() } },
        )
    }
}

@LightPreview
@DarkPreview
@Composable
private fun AppTextFieldPreview() {
    CajaClaraTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppTextField(rememberTextFieldState("Café molido"), label = "Nombre")
            AppTextField(rememberTextFieldState(), label = "SKU", placeholder = "Opcional", tooltip = "Código interno del producto.")
            AppTextField(rememberTextFieldState(), label = "Descripción", singleLine = false, minLines = 4)
        }
    }
}
