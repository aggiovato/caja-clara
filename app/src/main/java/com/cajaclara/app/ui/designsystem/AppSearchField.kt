package com.cajaclara.app.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** Search input: [AppTextField] in "search mode" (leading search icon, no label). */
@Composable
fun AppSearchField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    AppTextField(
        state = state,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = Icons.Filled.Search,
    )
}

@LightPreview
@DarkPreview
@Composable
private fun AppSearchFieldPreview() {
    CajaClaraTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppSearchField(rememberTextFieldState(), "Buscar producto", Modifier.fillMaxWidth())
            AppSearchField(rememberTextFieldState("Café"), "Buscar producto", Modifier.fillMaxWidth())
        }
    }
}
