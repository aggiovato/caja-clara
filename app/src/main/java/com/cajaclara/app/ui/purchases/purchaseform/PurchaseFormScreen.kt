package com.cajaclara.app.ui.purchases.purchaseform

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.ui.designsystem.AppCard
import com.cajaclara.app.ui.designsystem.AppEmptyState
import com.cajaclara.app.ui.designsystem.AppLoadingState
import com.cajaclara.app.ui.designsystem.AppMoneyText
import com.cajaclara.app.ui.designsystem.AppPrimaryButton
import com.cajaclara.app.ui.designsystem.AppSearchField
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.preview.PreviewSamples
import com.cajaclara.app.ui.theme.CajaClaraTheme
import com.cajaclara.app.ui.purchases.purchaseform.components.AddPurchaseLineDialog

@Composable
fun PurchaseFormScreen(
    onDone: () -> Unit,
    viewModel: PurchaseFormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val queryState = rememberTextFieldState()
    LaunchedEffect(Unit) {
        snapshotFlow { queryState.text.toString() }.collect(viewModel::onQueryChange)
    }
    LaunchedEffect(state.saved) { if (state.saved) onDone() }
    PurchaseFormScreen(
        state = state,
        queryState = queryState,
        onAddLine = viewModel::addLine,
        onRemoveLine = viewModel::removeLine,
        onConfirm = viewModel::confirm,
        onErrorShown = viewModel::onErrorShown,
        onBack = onDone,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseFormScreen(
    state: PurchaseFormUiState,
    queryState: TextFieldState,
    onAddLine: (Long, Int, com.cajaclara.app.core.money.Money, Boolean) -> Unit,
    onRemoveLine: (Long) -> Unit,
    onConfirm: () -> Unit,
    onErrorShown: () -> Unit,
    onBack: () -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    var adding by remember { mutableStateOf<Product?>(null) }
    LaunchedEffect(state.error) { state.error?.let { snackbar.showSnackbar(it); onErrorShown() } }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Registrar compra") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        bottomBar = { if (state.hasItems) InvestmentBar(state, onConfirm) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxWidth()) {
            AppSearchField(
                state = queryState,
                placeholder = "Buscar producto",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )
            when {
                state.isLoading -> AppLoadingState()
                state.products.isEmpty() -> AppEmptyState(
                    title = "Sin resultados",
                    subtitle = "No hay productos que coincidan",
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.products, key = { it.id.value }) { product ->
                        val draft = state.cart[product.id.value]
                        PurchaseProductRow(
                            product = product,
                            quantity = draft?.quantity,
                            onAdd = { adding = product },
                            onRemove = { onRemoveLine(product.id.value) },
                        )
                    }
                }
            }
        }
    }

    adding?.let { product ->
        AddPurchaseLineDialog(
            product = product,
            onConfirm = { qty, cost, update ->
                onAddLine(product.id.value, qty, cost, update)
                adding = null
            },
            onDismiss = { adding = null },
        )
    }
}

@Composable
private fun PurchaseProductRow(product: Product, quantity: Int?, onAdd: () -> Unit, onRemove: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row {
                    Text("Coste ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    AppMoneyText(product.currentCost, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("  ·  Stock ${product.stockQuantity.value}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (quantity != null) {
                    Text("x$quantity", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                // Same "+" icon for both states; it rotates to a "×" (135°) when in the cart.
                val rotation by animateFloatAsState(if (quantity != null) 135f else 0f, label = "addRemove")
                IconButton(onClick = { if (quantity != null) onRemove() else onAdd() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = if (quantity != null) "Quitar de la compra" else "Añadir a la compra",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.rotate(rotation),
                    )
                }
            }
        }
    }
}

@Composable
private fun InvestmentBar(state: PurchaseFormUiState, onConfirm: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text("Inversión", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AppMoneyText(
                    state.total,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            AppPrimaryButton(text = "Registrar", onClick = onConfirm, enabled = !state.isSaving)
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun PurchaseFormScreenPreview() {
    CajaClaraTheme {
        PurchaseFormScreen(
            state = PurchaseFormUiState(products = PreviewSamples.products(), isLoading = false),
            queryState = rememberTextFieldState(),
            onAddLine = { _, _, _, _ -> },
            onRemoveLine = {},
            onConfirm = {},
            onErrorShown = {},
            onBack = {},
        )
    }
}
