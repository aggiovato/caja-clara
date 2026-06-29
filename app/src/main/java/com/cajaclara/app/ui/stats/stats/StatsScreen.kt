package com.cajaclara.app.ui.stats.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.ui.designsystem.AppCard
import com.cajaclara.app.ui.designsystem.AppLoadingState
import com.cajaclara.app.ui.designsystem.AppMoneyText
import com.cajaclara.app.ui.stats.stats.components.ProfitBarChart
import com.cajaclara.app.ui.stats.stats.components.SalesCostLineChart

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StatsScreen(state = state, onRangeSelected = viewModel::onRangeSelected)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsScreen(state: StatsUiState, onRangeSelected: (Int) -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        if (state.isLoading) {
            AppLoadingState()
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            state.dailyBalance?.let { TodayCard(it) }

            Text("Ganancia por día", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            RangeSelector(selected = state.rangeDays, onRangeSelected = onRangeSelected)

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Total ${state.rangeDays} días",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AppMoneyText(
                            state.rangeProfit,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    Text(
                        "Toca una barra para ver el detalle",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ProfitBarChart(points = state.salesPoints)
                }
            }

            Text("Ventas y costes por día", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    SalesCostLineChart(points = state.salesPoints)
                }
            }
        }
    }
}

@Composable
private fun TodayCard(balance: DailyBalance) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Hoy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("Ganancia", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AppMoneyText(
                    balance.salesProfit,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            StatRow("Ingresos", balance.salesRevenue)
            StatRow("Coste", balance.salesCost)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Ventas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${balance.salesCount} · ${balance.productsSoldCount} uds.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StatRow(label: String, amount: Money) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        AppMoneyText(amount, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeSelector(selected: Int, onRangeSelected: (Int) -> Unit) {
    val options = listOf(7 to "7 días", 30 to "30 días")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (days, label) ->
            FilterChip(
                selected = selected == days,
                onClick = { onRangeSelected(days) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}
