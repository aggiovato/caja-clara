package com.cajaclara.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cajaclara.app.ui.designsystem.EmptyState
import com.cajaclara.app.ui.products.productform.ProductFormScreen
import com.cajaclara.app.ui.products.products.ProductsScreen

/**
 * App root: a bottom-bar [Scaffold] hosting the four top-level tabs plus the product form.
 * The bar shows only on the tab destinations; the form is a full screen without it.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val route = currentRoute?.destination?.route
    val showBottomBar = TopTab.entries.any { it.route == route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    TopTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    // Avoid building up a back stack of tabs; keep one of each.
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.PRODUCTS,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.HOME) { EmptyState(title = "Inicio", subtitle = "Próximamente") }
            composable(Routes.PRODUCTS) {
                ProductsScreen(onAddProduct = { navController.navigate(Routes.PRODUCT_FORM) })
            }
            composable(Routes.SALES) { EmptyState(title = "Ventas", subtitle = "Próximamente") }
            composable(Routes.STATS) { EmptyState(title = "Estadísticas", subtitle = "Próximamente") }
            composable(Routes.PRODUCT_FORM) {
                ProductFormScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}
