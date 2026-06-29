package com.cajaclara.app.ui.sales.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cajaclara.app.ui.designsystem.AppEmptyState
import com.cajaclara.app.ui.designsystem.AppLoadingState
import com.cajaclara.app.ui.designsystem.AppMoneyText
import com.cajaclara.app.ui.designsystem.AppPrimaryButton
import com.cajaclara.app.ui.sales.sales.components.DailySalesSheet
import com.cajaclara.app.ui.sales.sales.components.SaleProductItem

@Composable
fun SalesScreen(viewModel: SalesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SalesScreen(
        state = state,
        onAdd = viewModel::add,
        onRemove = viewModel::remove,
        onConfirm = viewModel::confirmSale,
        onCloseCash = viewModel::closeCash,
        onErrorShown = viewModel::onErrorShown,
        onSoldShown = viewModel::onSoldShown,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesScreen(
    state: SalesUiState,
    onAdd: (Long, Int) -> Unit,
    onRemove: (Long) -> Unit,
    onConfirm: () -> Unit,
    onCloseCash: (String) -> Unit,
    onErrorShown: () -> Unit,
    onSoldShown: () -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    var showSheet by remember { mutableStateOf(false) }
    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); onErrorShown() }
    }
    LaunchedEffect(state.justSold) {
        if (state.justSold) { snackbar.showSnackbar("Venta registrada"); onSoldShown() }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Ventas") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Ventas de hoy")
            }
        },
        bottomBar = { if (state.hasItems) CartBar(state, onConfirm) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxWidth()) {
            when {
                state.isLoading -> AppLoadingState()
                state.products.isEmpty() -> AppEmptyState(
                    title = "No hay productos a la venta",
                    subtitle = "Activa o reabastece productos para venderlos",
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.products, key = { it.id.value }) { product ->
                        SaleProductItem(
                            product = product,
                            categoryName = null,
                            quantityInCart = state.quantityOf(product.id.value),
                            onAdd = { onAdd(product.id.value, product.stockQuantity.value) },
                            onRemove = { onRemove(product.id.value) },
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            DailySalesSheet(
                state = state,
                isSaving = state.isSaving,
                onClose = onCloseCash,
            )
        }
    }
}

@Composable
private fun CartBar(state: SalesUiState, onConfirm: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${state.itemCount} ${if (state.itemCount == 1) "artículo" else "artículos"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppMoneyText(
                    state.total,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
            AppPrimaryButton(
                text = "Cobrar",
                onClick = onConfirm,
                enabled = !state.isSaving,
            )
        }
    }
}
