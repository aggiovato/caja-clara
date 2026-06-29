package com.cajaclara.app.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette (section 5 of the roadmap). Clean small-shop identity, backgrounds
// almost always white/gray. Names describe the actual hue, not the role: the primary
// accent is TEAL in light mode and ORANGE in dark mode (see Orange below).

// --- Teal (primary accent) ---
val Teal = Color(0xFF009688)         // Light primary; reused as dark secondary
val TealMist = Color(0xFFEDFCFA)     // Light primaryContainer; dark onPrimaryContainer
val TealDeep = Color(0xFF057C70)     // Light onPrimaryContainer
// Teal variants for an all-teal dark primary (alternative: Orange, see below).
val TealBright = Color(0xFF4DB6AC)       // Dark primary (lighter teal pops on dark)
val TealContainerDark = Color(0xFF134E48) // Dark primaryContainer
val TealInk = Color(0xFF00251F)          // Text/icon on the dark teal primary

// --- Warm neutrals ---
val WarmWhite = Color(0xFFFAFAF8)      // App background
val White = Color(0xFFFFFFFF)          // Surface
val CloudGray = Color(0xFFF3F4F6)      // Secondary surface
val SoftGray = Color(0xFFE5E7EB)       // Border
val Graphite = Color(0xFF1F2937)       // Primary text
val SlateGray = Color(0xFF6B7280)      // Secondary text
val MidGray = Color(0xFF9CA3AF)        // Muted text

// --- Neutral surface containers (tooltips, dialogs, menus, raised chips/buttons/thumbnails) ---
// Light: progressively darker than white so raised neutrals lift off the background.
val SurfaceContainerLowLight = Color(0xFFFAFAFB)
val SurfaceContainerLight = Color(0xFFF2F3F5)
val SurfaceContainerHighLight = Color(0xFFEAECEF)
val SurfaceContainerHighestLight = Color(0xFFE3E5E9)
// Dark: progressively lighter than the dark surface.
val SurfaceContainerLowDark = Color(0xFF1B2027)
val SurfaceContainerDark = Color(0xFF22272F)
val SurfaceContainerHighDark = Color(0xFF2A303A)
val SurfaceContainerHighestDark = Color(0xFF323845)

// --- Semantic / states ---
val ProfitGreen = Color(0xFF30795A)    // Success / profit / positive margin
val CoralRed = Color(0xFFE05252)       // Error / sold out / loss
val WarningAmber = Color(0xFFF2B84B)   // Warning / low margin
val InfoBlue = Color(0xFF3B82A0)       // Info / stats data (charts)

// --- Light derived containers (chips: "En stock", "Agotado", low margin) ---
val GreenContainer = Color(0xFFDCF1E7)
val RedContainer = Color(0xFFFBE3E3)
val AmberContainer = Color(0xFFFCEFCF)

// Deep tints for text on light containers (on*Container, light mode).
val DeepGreen = Color(0xFF15663F)
val DeepRed = Color(0xFF8A2B2B)
val DeepAmber = Color(0xFF8A5A00)

// --- Dark-mode variants ---
val Orange = Color(0xFFF5AF6C)              // Dark primary
val OrangeContainerDark = Color(0xFF966635) // Dark primaryContainer (brownish orange)
val DarkBackground = Color(0xFF171A21)
val DarkSurface = Color(0xFF20252C)
val DarkSurfaceVariant = Color(0xFF2A2F36)
val DarkBorder = Color(0xFF3A3F46)
// Dark secondary reuses Teal (#009688), defined above.
val AmberDark = Color(0xFFEAC377)
val RedDark = Color(0xFFDC8F8F)
val GreenContainerDark = Color(0xFF13392A)
val AmberContainerDark = Color(0xFF4A3A12)
val RedContainerDark = Color(0xFF4A2020)

// Near-black tints for text on accents in dark mode (on*, dark mode).
val OrangeInk = Color(0xFF2A1A08)
val GreenInk = Color(0xFF06251A)
val AmberInk = Color(0xFF2A1F08)
val RedInk = Color(0xFF2A0A0A)
