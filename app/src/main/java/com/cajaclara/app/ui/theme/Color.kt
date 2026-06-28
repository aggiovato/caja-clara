package com.cajaclara.app.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette (section 5 of the roadmap).
// Warm, clean small-shop identity: orange is the ACCENT (actions and active
// selection), backgrounds are almost always white/gray — never massive orange.

// --- Orange (primary accent) ---
val OrangeLight = Color(0xFFF59E42)    // Primary
val OrangeCream = Color(0xFFFFF3E6)    // Soft primary (container/selection)
val OrangeToasted = Color(0xFFEA7A1F)  // Primary hover/pressed

// --- Warm neutrals ---
val WarmWhite = Color(0xFFFAFAF8)      // App background
val White = Color(0xFFFFFFFF)          // Surface
val CloudGray = Color(0xFFF3F4F6)      // Secondary surface
val SoftGray = Color(0xFFE5E7EB)       // Border
val Graphite = Color(0xFF1F2937)       // Primary text
val SlateGray = Color(0xFF6B7280)      // Secondary text
val MidGray = Color(0xFF9CA3AF)        // Muted text

// --- Semantic / states ---
val ProfitGreen = Color(0xFF22A06B)    // Success / profit / positive margin
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

// --- Dark-mode variants (the palette is light; these keep the warmth) ---
val OrangeDark = Color(0xFFF5A65A)
val OrangeContainerDark = Color(0xFF6B3F12)
val DarkBackground = Color(0xFF14171C)
val DarkSurface = Color(0xFF1B1F24)
val DarkSurfaceVariant = Color(0xFF2A2F36)
val DarkBorder = Color(0xFF3A3F46)
val GreenDark = Color(0xFF4CC08A)
val AmberDark = Color(0xFFF2C46B)
val RedDark = Color(0xFFF08080)
val GreenContainerDark = Color(0xFF13392A)
val AmberContainerDark = Color(0xFF4A3A12)
val RedContainerDark = Color(0xFF4A2020)

// Near-black tints for text on accents in dark mode (on*, dark mode).
val OrangeInk = Color(0xFF2A1A08)
val GreenInk = Color(0xFF06251A)
val AmberInk = Color(0xFF2A1F08)
val RedInk = Color(0xFF2A0A0A)
