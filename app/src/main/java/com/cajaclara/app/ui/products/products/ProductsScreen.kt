package com.cajaclara.app.ui.products.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.ui.designsystem.AppEmptyState
import com.cajaclara.app.ui.designsystem.AppLoadingState
import com.cajaclara.app.ui.designsystem.AppSearchField
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.products.products.components.ProductListItem
import com.cajaclara.app.ui.products.products.components.StatusFilters
import com.cajaclara.app.ui.products.products.components.previewProducts
import com.cajaclara.app.ui.theme.CajaClaraTheme

@Composable
fun ProductsScreen(
    onAddProduct: () -> Unit,
    onEditProduct: (ProductId) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val queryState = rememberTextFieldState()
    LaunchedEffect(Unit) {
        // One-way: field -> ViewModel. The field never reads the query back, so its
        // text/cursor stay local and synchronous (no async glitches).
        snapshotFlow { queryState.text.toString() }
            .collect { viewModel.onQueryChange(it) }
    }
    ProductsScreen(
        state = state,
        queryState = queryState,
        onStatusChange = viewModel::onStatusChange,
        onAddProduct = onAddProduct,
        onEditProduct = onEditProduct,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(
    state: ProductsUiState,
    queryState: TextFieldState,
    onStatusChange: (ProductStatus?) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (ProductId) -> Unit,
) {
    Scaffold(
        // The outer (navigation) Scaffold already applies the status-bar/nav-bar insets,
        // so this inner one must not add them again.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        // Transparent so the app's decorative background shows through.
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir producto")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxWidth()) {
            AppSearchField(
                state = queryState,
                placeholder = "Buscar producto",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )

            StatusFilters(selected = state.filter.status, onStatusChange = onStatusChange)

            when {
                state.isLoading -> AppLoadingState()
                state.products.isEmpty() -> AppEmptyState(
                    title = "No hay productos",
                    subtitle = "Pulsa + para crear el primero",
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.products, key = { it.id.value }) { product ->
                        val categoryName = product.categoryId?.let { state.categoryNames[it.value] }
                        ProductListItem(product, categoryName, onClick = { onEditProduct(product.id) })
                    }
                }
            }
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun ProductsScreenPreview() {
    CajaClaraTheme {
        ProductsScreen(
            state = ProductsUiState(products = previewProducts(), isLoading = false),
            queryState = rememberTextFieldState(),
            onStatusChange = {},
            onAddProduct = {},
            onEditProduct = {},
        )
    }
}

@LightPreview
@DarkPreview
@Composable
private fun ProductsScreenEmptyPreview() {
    CajaClaraTheme {
        ProductsScreen(
            state = ProductsUiState(isLoading = false),
            queryState = rememberTextFieldState(),
            onStatusChange = {},
            onAddProduct = {},
            onEditProduct = {},
        )
    }
}
