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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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

    private val query = MutableStateFlow("")

    // Only active (sellable) products appear in quick sale; filtered by the search query.
    private val activeProducts = query.flatMapLatest { q ->
        observeProducts(ProductFilter(status = ProductStatus.ACTIVE, query = q.ifBlank { null }))
    }

    // Ids of every currently sellable product (active, ignoring the search query), used to keep
    // the cart valid regardless of what's on screen.
    private val sellableIds: Flow<Set<Long>> =
        observeProducts(ProductFilter.ALL).map { list ->
            list.filter { it.status == ProductStatus.ACTIVE }.mapTo(HashSet()) { it.id.value }
        }

    // The current day, re-read periodically. Without this, `today` would be frozen to the day the
    // screen opened, so crossing midnight (screen left open, or returned to next day) would keep
    // showing the previous day's sales and let the cash close land on the wrong day.
    private val today: Flow<LocalDate> = flow {
        while (true) {
            emit(LocalDate.now(clock))
            delay(DAY_REFRESH_INTERVAL_MS.milliseconds)
        }
    }.distinctUntilChanged()

    private val dailySales = today.flatMapLatest { observeDailySales(it) }
    private val cashClose = today.flatMapLatest { observeCashClose(it) }

    private val cart = MutableStateFlow<Map<Long, Int>>(emptyMap())
    private val transient = MutableStateFlow(TransientState())

    init {
        // Prune the cart as soon as a product stops being sellable (paused, archived or sold out).
        // Otherwise, its stale id stays in the cart and blocks the sale with a "product not found"
        // error even though it's no longer shown.
        sellableIds
            .onEach { ids -> cart.update { c -> c.filterKeys { it in ids } } }
            .launchIn(viewModelScope)
    }

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

    private companion object {
        /** How often to re-check the current date so the day rolls over while the screen lives. */
        const val DAY_REFRESH_INTERVAL_MS = 60_000L
    }
}
