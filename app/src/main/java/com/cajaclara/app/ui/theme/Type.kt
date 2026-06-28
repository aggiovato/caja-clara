package com.cajaclara.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.cajaclara.app.R

// App typeface: Barlow (bundled in res/font so it works offline).
// Each weight is bound to its FontWeight; Compose picks the right one per style.
val Barlow = FontFamily(
    Font(R.font.barlow_regular, FontWeight.Normal),    // 400
    Font(R.font.barlow_medium, FontWeight.Medium),     // 500
    Font(R.font.barlow_semibold, FontWeight.SemiBold), // 600
    Font(R.font.barlow_bold, FontWeight.Bold),         // 700
)

// Weight mapping by role (roadmap table):
//   Large amounts  -> display*  -> Bold 700
//   Titles         -> headline* -> SemiBold 600
//   Cards / metrics -> title*   -> Medium 500
//   Body text      -> body*     -> Regular 400
//   Small labels   -> label*    -> Medium 500
private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Barlow, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.Bold),
    displaySmall = base.displaySmall.copy(fontFamily = Barlow, fontWeight = FontWeight.Bold),

    headlineLarge = base.headlineLarge.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold),

    titleLarge = base.titleLarge.copy(fontFamily = Barlow, fontWeight = FontWeight.Medium),
    titleMedium = base.titleMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.Medium),
    titleSmall = base.titleSmall.copy(fontFamily = Barlow, fontWeight = FontWeight.Medium),

    bodyLarge = base.bodyLarge.copy(fontFamily = Barlow, fontWeight = FontWeight.Normal),
    bodyMedium = base.bodyMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.Normal),
    bodySmall = base.bodySmall.copy(fontFamily = Barlow, fontWeight = FontWeight.Normal),

    labelLarge = base.labelLarge.copy(fontFamily = Barlow, fontWeight = FontWeight.Medium),
    labelMedium = base.labelMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = Barlow, fontWeight = FontWeight.Medium),
)
