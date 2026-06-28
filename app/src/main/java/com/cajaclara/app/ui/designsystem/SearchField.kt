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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** Search input: [AppTextField] in "search mode" (leading search icon, no label). */
@Composable
fun SearchField(
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

@Preview(showBackground = true)
@Composable
private fun SearchFieldPreview() {
    CajaClaraTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SearchField(rememberTextFieldState(), "Buscar producto", Modifier.fillMaxWidth())
            SearchField(rememberTextFieldState("Café"), "Buscar producto", Modifier.fillMaxWidth())
        }
    }
}
