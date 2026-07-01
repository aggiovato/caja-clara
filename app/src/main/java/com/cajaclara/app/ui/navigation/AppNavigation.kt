package com.cajaclara.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cajaclara.app.ui.designsystem.AppBackground
import com.cajaclara.app.ui.designsystem.AppEmptyState
import com.cajaclara.app.ui.home.home.HomeScreen
import com.cajaclara.app.ui.products.productform.ProductFormScreen
import com.cajaclara.app.ui.sales.sales.SalesScreen
import com.cajaclara.app.ui.products.products.ProductsScreen
import com.cajaclara.app.ui.purchases.purchaseform.PurchaseFormScreen
import com.cajaclara.app.ui.stats.stats.StatsScreen

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

    AppBackground {
    Scaffold(
        // Transparent so the decorative background shows through; screens are transparent too.
        containerColor = Color.Transparent,
        // A transparent container yields no content color, so set a readable default.
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            // Animate the bar in/out so it slides with screen transitions instead of popping.
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
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
            composable(Routes.HOME) {
                HomeScreen(
                    onNewSale = {
                        navController.navigate(Routes.SALES) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRegisterPurchase = { navController.navigate(Routes.PURCHASE_FORM) },
                    onNewProduct = { navController.navigate(Routes.PRODUCT_FORM) },
                    onSettings = { navController.navigate(Routes.SETTINGS) },
                )
            }
            composable(Routes.PRODUCTS) {
                ProductsScreen(
                    onAddProduct = { navController.navigate(Routes.PRODUCT_FORM) },
                    onEditProduct = { id -> navController.navigate(Routes.editProduct(id.value)) },
                )
            }
            composable(Routes.SALES) {
                SalesScreen(onRegisterPurchase = { navController.navigate(Routes.PURCHASE_FORM) })
            }
            composable(Routes.STATS) { StatsScreen() }
            composable(
                route = Routes.PRODUCT_FORM_PATTERN,
                arguments = listOf(
                    navArgument("productId") { type = NavType.LongType; defaultValue = -1L },
                ),
            ) {
                ProductFormScreen(onDone = { navController.popBackStack() })
            }
            composable(Routes.PURCHASE_FORM) {
                PurchaseFormScreen(onDone = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                AppEmptyState(title = "Configuración", subtitle = "Próximamente")
            }
        }
    }
    }
}
