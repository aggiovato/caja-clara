package com.cajaclara.app.ui.home.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cajaclara.app.feature.stats.domain.model.BusinessInsights
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.model.TopProduct
import com.cajaclara.app.ui.designsystem.AppCard
import com.cajaclara.app.ui.designsystem.AppMoneyText
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.CajaClaraTheme
import com.cajaclara.app.core.money.Money

@Composable
fun HomeScreen(
    onNewSale: () -> Unit,
    onRegisterPurchase: () -> Unit,
    onNewProduct: () -> Unit,
    onSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onNewSale = onNewSale,
        onRegisterPurchase = onRegisterPurchase,
        onNewProduct = onNewProduct,
        onSettings = onSettings,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeUiState,
    onNewSale: () -> Unit,
    onRegisterPurchase: () -> Unit,
    onNewProduct: () -> Unit,
    onSettings: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text("Caja Clara") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSettings,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Configuración")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AccountCard(state.accountBalance)
            state.today?.let { TodayCard(it, state.isCashClosed) }
            if (state.hasStockAlerts) StockAlerts(state.soldOutCount, state.lowStockCount)
            if (state.hasInsights) InsightsCard(state.insights)

            Text("Acciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction("Vender", Icons.AutoMirrored.Filled.ReceiptLong, onNewSale, Modifier.weight(1f))
                QuickAction("Comprar", Icons.Filled.ShoppingCart, onRegisterPurchase, Modifier.weight(1f))
                QuickAction("Producto", Icons.Filled.Add, onNewProduct, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AccountCard(balance: Money) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Cuenta del negocio", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            AppMoneyText(
                balance,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = if (balance.isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
            Text("Ventas acumuladas menos compras", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TodayCard(balance: DailyBalance, isCashClosed: Boolean) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Hoy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (isCashClosed) "✓ Caja cuadrada" else "Caja abierta",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isCashClosed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("Ganancia", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AppMoneyText(
                    balance.salesProfit,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(
                "${balance.salesCount} ventas · ${balance.productsSoldCount} uds.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StockAlerts(soldOut: Int, lowStock: Int) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            Column {
                Text("Atención al stock", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                val parts = buildList {
                    if (soldOut > 0) add("$soldOut agotado${if (soldOut == 1) "" else "s"}")
                    if (lowStock > 0) add("$lowStock con stock bajo")
                }
                Text(parts.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun InsightsCard(insights: BusinessInsights) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Rentabilidad del negocio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            insights.profitabilityPercent?.let { pct ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Margen medio", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(sustainabilityHint(pct), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "${"%.0f".format(pct)}%",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (pct <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            insights.topSelling?.let { TopProductRow("Más vendido", it, showUnits = true) }
            insights.mostProfitable?.let { TopProductRow("Más rentable", it, showUnits = false) }
        }
    }
}

@Composable
private fun TopProductRow(label: String, product: TopProduct, showUnits: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        if (showUnits) {
            Text("${product.units} uds.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        } else {
            AppMoneyText(product.profit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

/** A short reading of the business margin for the shopkeeper. */
private fun sustainabilityHint(pct: Double): String = when {
    pct <= 0 -> "Estás vendiendo con pérdida"
    pct < 15 -> "Margen ajustado"
    pct < 35 -> "Rentabilidad saludable"
    else -> "Rentabilidad alta"
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun HomeScreenPreview() {
    val balance = DailyBalance(
        date = java.time.LocalDate.of(2026, 6, 30),
        salesRevenue = Money.fromPesos("40,00"),
        salesCost = Money.fromPesos("16,00"),
        manualProfit = Money.ZERO,
        salesCount = 3,
        productsSoldCount = 5,
    )
    CajaClaraTheme {
        HomeScreen(
            state = HomeUiState(
                accountBalance = Money.fromPesos("1.250,00"),
                today = balance,
                isCashClosed = false,
                soldOutCount = 2,
                lowStockCount = 3,
                insights = BusinessInsights(
                    profitabilityPercent = 28.0,
                    topSelling = TopProduct("Café molido", units = 42, profit = Money.fromPesos("60,00")),
                    mostProfitable = TopProduct("Agua 1.5L", units = 30, profit = Money.fromPesos("90,00")),
                ),
                isLoading = false,
            ),
            onNewSale = {},
            onRegisterPurchase = {},
            onNewProduct = {},
            onSettings = {},
        )
    }
}
