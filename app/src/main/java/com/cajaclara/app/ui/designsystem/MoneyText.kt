package com.cajaclara.app.ui.designsystem

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** Renders a [Money] amount in the app's format (e.g. `12,50 CUP`). */
@Composable
fun MoneyText(
    money: Money,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    Text(text = money.format(), modifier = modifier, style = style, color = color)
}

@Preview(showBackground = true)
@Composable
private fun MoneyTextPreview() {
    CajaClaraTheme {
        MoneyText(Money.fromPesos("1.234,50"))
    }
}
