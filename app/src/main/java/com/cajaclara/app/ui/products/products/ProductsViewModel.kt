package com.cajaclara.app.ui.products.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    observeProducts: ObserveProductsUseCase,
    observeCategories: ObserveCategoriesUseCase,
) : ViewModel() {

    private val filter = MutableStateFlow(ProductFilter.ALL)

    val uiState: StateFlow<ProductsUiState> = combine(
        filter,
        filter.flatMapLatest { observeProducts(it) },
        observeCategories(),
    ) { currentFilter, products, categories ->
        ProductsUiState(
            products = products,
            filter = currentFilter,
            categoryNames = categories.associate { it.id.value to it.name },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductsUiState(),
    )

    /** Update the free-text search over name/SKU; blank clears it. */
    fun onQueryChange(query: String) =
        filter.update { it.copy(query = query.ifBlank { null }) }

    /** Filter by status; `null` shows all (the "Todos" tab). */
    fun onStatusChange(status: ProductStatus?) =
        filter.update { it.copy(status = status) }
}
