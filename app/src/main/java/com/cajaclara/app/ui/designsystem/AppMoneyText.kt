package com.cajaclara.app.ui.designsystem

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.CajaClaraTheme

/**
 * Renders [formatted] money text with a trailing currency code (e.g. " CUP") shrunk to 0.7em
 * (70% of the surrounding size) so it reads as a symbol. Strings without the code are returned
 * unchanged, so it is safe for plain values (e.g. a stock count). Shared by money displays.
 */
fun moneyAnnotatedString(formatted: String): AnnotatedString {
    val code = " ${Money.CURRENCY_CODE}"
    return buildAnnotatedString {
        if (formatted.endsWith(code)) {
            append(formatted.removeSuffix(code))
            withStyle(SpanStyle(fontSize = 0.7.em)) { append(code) }
        } else {
            append(formatted)
        }
    }
}

/**
 * Renders a [Money] amount in the app's format (e.g. `12,50 CUP`), with the `CUP` currency
 * code rendered smaller than the number so it reads as a symbol, not part of the figure.
 */
@Composable
fun AppMoneyText(
    money: Money,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    Text(
        text = moneyAnnotatedString(money.format()),
        modifier = modifier,
        style = style,
        color = color,
    )
}

@LightPreview
@DarkPreview
@Composable
private fun AppMoneyTextPreview() {
    CajaClaraTheme {
        AppMoneyText(Money.fromPesos("1.234,50"))
    }
}
