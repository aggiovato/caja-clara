package com.cajaclara.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Single corner radius used across the whole app: cards, buttons, inputs, chips. */
val AppCornerRadius = 8.dp

/** Every Material 3 shape size maps to the same radius, so all surfaces look consistent. */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(AppCornerRadius),
    small = RoundedCornerShape(AppCornerRadius),
    medium = RoundedCornerShape(AppCornerRadius),
    large = RoundedCornerShape(AppCornerRadius),
    extraLarge = RoundedCornerShape(AppCornerRadius),
)
