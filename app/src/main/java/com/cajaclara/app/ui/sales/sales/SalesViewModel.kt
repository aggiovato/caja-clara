package com.cajaclara.app.ui.sales.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import com.cajaclara.app.feature.sales.domain.usecase.CloseCashUseCase
import com.cajaclara.app.feature.sales.domain.usecase.ObserveCashCloseUseCase
import com.cajaclara.app.feature.sales.domain.usecase.ObserveDailySalesUseCase
import com.cajaclara.app.feature.sales.domain.usecase.RegisterSaleUseCase
import com.cajaclara.app.feature.sales.domain.usecase.SaleItem
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
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
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SalesViewModel @Inject constructor(
    observeProducts: ObserveProductsUseCase,
    observeDailySales: ObserveDailySalesUseCase,
    observeCashClose: ObserveCashCloseUseCase,
    private val registerSale: RegisterSaleUseCase,
    private val closeCashUseCase: CloseCashUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now(clock)
    private val query = MutableStateFlow("")

    // Only active (sellable) products appear in quick sale; filtered by the search query.
    private val activeProducts = query.flatMapLatest { q ->
        observeProducts(ProductFilter(status = ProductStatus.ACTIVE, query = q.ifBlank { null }))
    }
    private val dailySales = observeDailySales(today)
    private val cashClose = observeCashClose(today)

    private val cart = MutableStateFlow<Map<Long, Int>>(emptyMap())
    private val transient = MutableStateFlow(TransientState())

    val state: StateFlow<SalesUiState> =
        combine(activeProducts, cart, transient, dailySales, cashClose) { products, cart, t, sales, close ->
            // Drop cart entries whose product is no longer active/available.
            val validCart = cart.filterKeys { id -> products.any { it.id.value == id } }
            SalesUiState(
                products = products,
                cart = validCart,
                isLoading = false,
                isSaving = t.isSaving,
                error = t.error,
                justSold = t.justSold,
                dailySales = sales,
                cashClose = close,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SalesUiState())

    fun onQueryChange(text: String) { query.value = text }

    /** Add one unit, capped at the product's available stock. */
    fun add(productId: Long, available: Int) {
        cart.update { c ->
            val current = c[productId] ?: 0
            if (current >= available) c else c + (productId to current + 1)
        }
    }

    fun remove(productId: Long) {
        cart.update { c ->
            val current = c[productId] ?: 0
            when {
                current <= 1 -> c - productId
                else -> c + (productId to current - 1)
            }
        }
    }

    fun clearCart() = cart.update { emptyMap() }

    fun onErrorShown() = transient.update { it.copy(error = null) }

    fun confirmSale() {
        val items = cart.value.map { (id, qty) -> SaleItem(ProductId(id), Quantity(qty)) }
        if (items.isEmpty()) return
        viewModelScope.launch {
            transient.update { it.copy(isSaving = true, error = null) }
            runCatching { registerSale(items) }
                .onSuccess {
                    cart.value = emptyMap()
                    transient.update { it.copy(isSaving = false, justSold = true) }
                }
                .onFailure { e ->
                    transient.update { it.copy(isSaving = false, error = e.message ?: "No se pudo registrar la venta") }
                }
        }
    }

    fun onSoldShown() = transient.update { it.copy(justSold = false) }

    /** Close (or re-close) today's cash with the real [countedCash] in pesos text. */
    fun closeCash(countedCashText: String) {
        val counted = Money.fromPesosOrNull(countedCashText)
        if (counted == null || counted.isNegative) {
            transient.update { it.copy(error = "Importe contado no válido") }
            return
        }
        viewModelScope.launch {
            transient.update { it.copy(isSaving = true, error = null) }
            runCatching { closeCashUseCase(counted) }
                .onSuccess { transient.update { it.copy(isSaving = false) } }
                .onFailure { e -> transient.update { it.copy(isSaving = false, error = e.message ?: "No se pudo cuadrar la caja") } }
        }
    }

    private data class TransientState(
        val isSaving: Boolean = false,
        val error: String? = null,
        val justSold: Boolean = false,
    )
}
