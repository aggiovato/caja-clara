package com.cajaclara.app.ui.products.productform

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.data.CameraTarget
import com.cajaclara.app.feature.products.data.ImageStore
import com.cajaclara.app.feature.products.data.ShareImageStore
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.ArchiveProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.CreateProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.GetProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.MinMarginViolationException
import com.cajaclara.app.feature.products.domain.usecase.NewProduct
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import com.cajaclara.app.feature.products.domain.usecase.PauseProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.ProductEdits
import com.cajaclara.app.feature.products.domain.usecase.ResumeProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.SuggestSkuUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductCostUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductPvpUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductUseCase
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.cajaclara.app.feature.stock.domain.usecase.AdjustStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val createProduct: CreateProductUseCase,
    private val getProduct: GetProductUseCase,
    private val updateProduct: UpdateProductUseCase,
    private val updateCost: UpdateProductCostUseCase,
    private val updatePvp: UpdateProductPvpUseCase,
    private val pauseProduct: PauseProductUseCase,
    private val resumeProduct: ResumeProductUseCase,
    private val archiveProduct: ArchiveProductUseCase,
    private val adjustStock: AdjustStockUseCase,
    private val suggestSku: SuggestSkuUseCase,
    observeCategories: ObserveCategoriesUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val imageStore: ImageStore,
    private val shareImageStore: ShareImageStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val editId: ProductId? =
        savedStateHandle.get<Long>(ARG_PRODUCT_ID)?.takeIf { it > 0 }?.let(::ProductId)

    private var loadedCategoryId: CategoryId? = null

    private val _state = MutableStateFlow(ProductFormUiState(isEdit = editId != null, productId = editId))
    val state: StateFlow<ProductFormUiState> = _state.asStateFlow()

    init {
        if (editId != null) loadProduct(editId)

        observeCategories()
            .onEach { categories ->
                _state.update { s ->
                    val selected = if (s.isEdit) {
                        categories.firstOrNull { it.id == loadedCategoryId } ?: s.selectedCategory
                    } else {
                        s.selectedCategory
                            ?: categories.firstOrNull { it.name == DEFAULT_CATEGORY }
                            ?: categories.firstOrNull()
                    }
                    s.copy(categories = categories, selectedCategory = selected)
                }
            }
            .launchIn(viewModelScope)

        // Keep the shop address in state so the shareable image can show it.
        observeSettings()
            .onEach { settings -> _state.update { it.copy(storeAddress = settings.storeAddress) } }
            .launchIn(viewModelScope)
    }

    private fun loadProduct(id: ProductId) {
        viewModelScope.launch {
            val product = getProduct(id) ?: return@launch
            loadedCategoryId = product.categoryId
            _state.update { s ->
                s.copy(
                    currentCost = product.currentCost,
                    currentPvp = product.currentPvp,
                    currentStock = product.stockQuantity,
                    status = product.status,
                    imagePath = product.imagePath,
                    selectedCategory = s.categories.firstOrNull { it.id == product.categoryId } ?: s.selectedCategory,
                    prefill = ProductPrefill(product.name, product.sku.orEmpty(), product.description.orEmpty()),
                )
            }
        }
    }

    private fun reload() {
        val id = editId ?: return
        viewModelScope.launch {
            val product = getProduct(id) ?: return@launch
            _state.update {
                it.copy(
                    currentCost = product.currentCost,
                    currentPvp = product.currentPvp,
                    currentStock = product.stockQuantity,
                    status = product.status,
                )
            }
        }
    }

    fun onPrefillConsumed() = _state.update { it.copy(prefill = null) }

    /** Refresh the suggested SKU (shown as the SKU placeholder) from the current name. */
    fun onNameChanged(name: String) =
        _state.update { it.copy(skuSuggestion = suggestSku(name)) }

    fun onCategorySelected(category: Category) =
        _state.update { it.copy(selectedCategory = category) }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            val path = imageStore.saveProductImage(uri)
            _state.update { it.copy(imagePath = path) }
        }
    }

    fun newCameraTarget(): CameraTarget = imageStore.newCameraTarget()

    fun onPhotoTaken(path: String) = _state.update { it.copy(imagePath = path) }

    fun onRemoveImage() = _state.update { it.copy(imagePath = null) }

    fun onErrorShown() = _state.update { it.copy(error = null) }

    /** Persist the captured shareable image to cache; exposes its URI for the share intent. */
    fun shareProductImage(bitmap: Bitmap) {
        viewModelScope.launch {
            runCatching { shareImageStore.saveShareable(bitmap) }
                .onSuccess { uri -> _state.update { it.copy(shareImageUri = uri) } }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "No se pudo generar la imagen") } }
        }
    }

    fun onShareHandled() = _state.update { it.copy(shareImageUri = null) }

    /** Save: create a new product, or update the general fields of the one being edited. */
    fun save(
        name: String,
        costText: String,
        pvpText: String,
        stockText: String,
        sku: String,
        description: String,
    ) {
        if (_state.value.isEdit) {
            saveEdit(name, sku, description)
        } else {
            saveNew(name, costText, pvpText, stockText, sku, description)
        }
    }

    private fun saveEdit(name: String, sku: String, description: String) {
        if (name.isBlank()) {
            _state.update { it.copy(error = "El nombre es obligatorio") }
            return
        }
        val id = _state.value.productId ?: return
        launchSaving {
            updateProduct(
                id,
                ProductEdits(
                    name = name,
                    sku = resolveSku(sku, name),
                    categoryId = _state.value.selectedCategory?.id,
                    description = description.ifBlank { null },
                    imagePath = _state.value.imagePath,
                ),
            )
        }
    }

    private fun saveNew(
        name: String,
        costText: String,
        pvpText: String,
        stockText: String,
        sku: String,
        description: String,
    ) {
        val cost = Money.fromPesosOrNull(costText)
        val pvp = Money.fromPesosOrNull(pvpText)
        val error = when {
            name.isBlank() -> "El nombre es obligatorio"
            cost == null || cost.isNegative -> "Introduce un coste válido"
            pvp == null || pvp.isNegative -> "Introduce un PVP válido"
            else -> null
        }
        if (error != null) {
            _state.update { it.copy(error = error) }
            return
        }
        val stock = stockText.trim().toIntOrNull()?.takeIf { it >= 0 } ?: 0
        launchSaving {
            createProduct(
                NewProduct(
                    name = name,
                    cost = cost!!,
                    pvp = pvp!!,
                    stock = Quantity(stock),
                    sku = resolveSku(sku, name),
                    categoryId = _state.value.selectedCategory?.id,
                    description = description.ifBlank { null },
                    imagePath = _state.value.imagePath,
                ),
            )
        }
    }

    /** The typed SKU, or the name-based suggestion when left blank; null if both are empty. */
    private fun resolveSku(typed: String, name: String): String? =
        typed.trim().ifBlank { suggestSku(name) }.ifBlank { null }

    /** Change the cost (writes price history). Edit mode only. */
    fun changeCost(text: String) = changePrice(text) { id, money -> updateCost(id, money) }

    /** Change the PVP (writes price history). Edit mode only. */
    fun changePvp(text: String) = changePrice(text) { id, money -> updatePvp(id, money) }

    private inline fun changePrice(text: String, crossinline action: suspend (ProductId, Money) -> Unit) {
        val money = Money.fromPesosOrNull(text)
        if (money == null || money.isNegative) {
            _state.update { it.copy(error = "Importe no válido") }
            return
        }
        val id = _state.value.productId ?: return
        viewModelScope.launch {
            runCatching { action(id, money) }
                .onSuccess { reload() }
                .onFailure { e ->
                    val msg = when (e) {
                        is MinMarginViolationException ->
                            "El margen quedaría por debajo del mínimo (${e.minPercent.toInt()}%)"
                        else -> e.message ?: "No se pudo guardar"
                    }
                    _state.update { it.copy(error = msg) }
                }
        }
    }

    /** Set the stock to a new total; records the change and refreshes. Edit mode only. */
    fun changeStock(text: String) {
        val quantity = text.trim().toIntOrNull()
        if (quantity == null || quantity < 0) {
            _state.update { it.copy(error = "Cantidad no válida") }
            return
        }
        val id = _state.value.productId ?: return
        viewModelScope.launch {
            runCatching { adjustStock(id, Quantity(quantity)) }
                .onSuccess { reload() }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "No se pudo guardar") } }
        }
    }

    fun togglePause() {
        val id = _state.value.productId ?: return
        viewModelScope.launch {
            if (_state.value.status == ProductStatus.PAUSED) resumeProduct(id) else pauseProduct(id)
            reload()
        }
    }

    fun archive() {
        val id = _state.value.productId ?: return
        viewModelScope.launch {
            archiveProduct(id)
            _state.update { it.copy(saved = true) }
        }
    }

    private inline fun launchSaving(crossinline block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            runCatching { block() }
                .onSuccess { _state.update { it.copy(isSaving = false, saved = true) } }
                .onFailure { e ->
                    val msg = when (e) {
                        is MinMarginViolationException ->
                            "El margen (mín. ${e.minPercent.toInt()}%) no se cumple con este coste y PVP"
                        else -> e.message ?: "No se pudo guardar"
                    }
                    _state.update { it.copy(isSaving = false, error = msg) }
                }
        }
    }

    companion object {
        const val ARG_PRODUCT_ID = "productId"
        private const val DEFAULT_CATEGORY = "Otros"
    }
}
