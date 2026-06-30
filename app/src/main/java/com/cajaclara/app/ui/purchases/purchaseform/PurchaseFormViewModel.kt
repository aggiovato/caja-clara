package com.cajaclara.app.ui.purchases.purchaseform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.purchases.domain.usecase.PurchaseItem
import com.cajaclara.app.feature.purchases.domain.usecase.RegisterPurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PurchaseFormViewModel @Inject constructor(
    observeProducts: ObserveProductsUseCase,
    private val registerPurchase: RegisterPurchaseUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")

    // Any non-archived product can be restocked (active, sold out or paused); filtered by search.
    private val products = query.flatMapLatest { q ->
        observeProducts(ProductFilter(query = q.ifBlank { null }))
    }
    private val cart = MutableStateFlow<Map<Long, PurchaseDraft>>(emptyMap())
    private val transient = MutableStateFlow(TransientState())

    val state: StateFlow<PurchaseFormUiState> =
        combine(products, cart, transient) { products, cart, t ->
            val validCart = cart.filterKeys { id -> products.any { it.id.value == id } }
            PurchaseFormUiState(
                products = products,
                cart = validCart,
                isLoading = false,
                isSaving = t.isSaving,
                error = t.error,
                saved = t.saved,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PurchaseFormUiState())

    fun onQueryChange(text: String) { query.value = text }

    fun addLine(productId: Long, quantity: Int, unitCost: Money, updateCost: Boolean) {
        if (quantity <= 0) return
        cart.update { it + (productId to PurchaseDraft(quantity, unitCost, updateCost)) }
    }

    fun removeLine(productId: Long) = cart.update { it - productId }

    fun onErrorShown() = transient.update { it.copy(error = null) }

    fun confirm() {
        val items = cart.value.map { (id, d) ->
            PurchaseItem(ProductId(id), Quantity(d.quantity), d.unitCost, d.updateCost)
        }
        if (items.isEmpty()) return
        viewModelScope.launch {
            transient.update { it.copy(isSaving = true, error = null) }
            runCatching { registerPurchase(items) }
                .onSuccess { transient.update { it.copy(isSaving = false, saved = true) } }
                .onFailure { e -> transient.update { it.copy(isSaving = false, error = e.message ?: "No se pudo registrar la compra") } }
        }
    }

    private data class TransientState(
        val isSaving: Boolean = false,
        val error: String? = null,
        val saved: Boolean = false,
    )
}
