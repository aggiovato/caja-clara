package com.cajaclara.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.ui.graphics.vector.ImageVector

/** Navigation route keys (string-based for now; can move to type-safe routes later). */
object Routes {
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val SALES = "sales"
    const val STATS = "stats"
    const val PRODUCT_FORM = "product_form"
}

/** The four bottom-bar tabs, in display order. */
enum class TopTab(val route: String, val label: String, val icon: ImageVector) {
    HOME(Routes.HOME, "Inicio", Icons.Filled.Home),
    PRODUCTS(Routes.PRODUCTS, "Productos", Icons.Filled.Inventory2),
    SALES(Routes.SALES, "Ventas", Icons.Filled.ReceiptLong),
    STATS(Routes.STATS, "Estadísticas", Icons.Filled.BarChart),
}
