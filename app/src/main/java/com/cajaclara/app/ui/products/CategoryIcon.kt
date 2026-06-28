package com.cajaclara.app.ui.products

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Default icon for a product without an image, derived from its category name.
 * `null` (no category / unknown) falls back to a generic icon.
 */
fun categoryIcon(categoryName: String?): ImageVector = when (categoryName?.trim()?.lowercase()) {
    "bebidas" -> Icons.Filled.LocalDrink
    "alimentación", "alimentacion" -> Icons.Filled.Restaurant
    "limpieza" -> Icons.Filled.CleaningServices
    "papelería", "papeleria" -> Icons.Filled.Edit
    else -> Icons.Filled.Category
}
