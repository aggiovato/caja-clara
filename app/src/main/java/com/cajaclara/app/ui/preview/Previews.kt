package com.cajaclara.app.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotations to render a composable in both themes. Use both on a preview:
 *
 * ```
 * @LightPreview
 * @DarkPreview
 * @Composable
 * private fun FooPreview() = CajaClaraTheme { Foo() }
 * ```
 */
@Preview(name = "Light", showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_NO)
annotation class LightPreview

@Preview(name = "Dark", showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class DarkPreview
