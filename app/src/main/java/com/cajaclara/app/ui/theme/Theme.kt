package com.cajaclara.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = White,
    primaryContainer = TealMist,
    onPrimaryContainer = TealDeep,
    secondary = ProfitGreen,
    onSecondary = White,
    secondaryContainer = GreenContainer,
    onSecondaryContainer = DeepGreen,
    tertiary = WarningAmber,
    onTertiary = Graphite,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = DeepAmber,
    error = CoralRed,
    onError = White,
    errorContainer = RedContainer,
    onErrorContainer = DeepRed,
    background = WarmWhite,
    onBackground = Graphite,
    surface = White,
    onSurface = Graphite,
    surfaceVariant = CloudGray,
    onSurfaceVariant = SlateGray,
    surfaceContainerLowest = White,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    outline = SoftGray,
    outlineVariant = CloudGray,
)

private val DarkColorScheme = darkColorScheme(
    // All-teal primary for dark. To go back to the orange dark primary (which looked good),
    // swap these four for: Orange / OrangeInk / OrangeContainerDark / TealMist.
    primary = TealBright,
    onPrimary = TealInk,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = TealMist,
    secondary = Teal,
    onSecondary = GreenInk,
    secondaryContainer = GreenContainerDark,
    onSecondaryContainer = GreenContainer,
    tertiary = AmberDark,
    onTertiary = AmberInk,
    tertiaryContainer = AmberContainerDark,
    onTertiaryContainer = AmberContainer,
    error = RedDark,
    onError = RedInk,
    errorContainer = RedContainerDark,
    onErrorContainer = RedContainer,
    background = DarkBackground,
    onBackground = WarmWhite,
    surface = DarkSurface,
    onSurface = WarmWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MidGray,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    outline = DarkBorder,
    outlineVariant = DarkSurface,
)

@Composable
fun CajaClaraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Brand identity wins: dynamic color (Material You) is off by default so it never
    // overrides Caja Clara's warm palette.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
