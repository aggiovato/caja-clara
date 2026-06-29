package com.cajaclara.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector

/** Navigation route keys (string-based for now; can move to type-safe routes later). */
object Routes {
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val SALES = "sales"
    const val STATS = "stats"

    /** Create: navigate to [PRODUCT_FORM]; edit: [editProduct]. Registered as [PRODUCT_FORM_PATTERN]. */
    const val PRODUCT_FORM = "product_form"
    const val PRODUCT_FORM_PATTERN = "product_form?productId={productId}"
    fun editProduct(id: Long) = "product_form?productId=$id"
}

/** The four bottom-bar tabs, in display order. */
enum class TopTab(val route: String, val label: String, val icon: ImageVector) {
    HOME(Routes.HOME, "Inicio", Icons.Filled.Home),
    PRODUCTS(Routes.PRODUCTS, "Productos", Icons.Filled.Inventory2),
    SALES(Routes.SALES, "Ventas", Icons.AutoMirrored.Filled.ReceiptLong),
    STATS(Routes.STATS, "Estadísticas", Icons.Filled.BarChart),
}
