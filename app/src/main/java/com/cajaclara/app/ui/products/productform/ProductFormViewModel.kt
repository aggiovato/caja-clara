package com.cajaclara.app.ui.products.productform

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.data.CameraTarget
import com.cajaclara.app.feature.products.data.ImageStore
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.usecase.CreateProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.NewProduct
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val createProduct: CreateProductUseCase,
    observeCategories: ObserveCategoriesUseCase,
    private val imageStore: ImageStore,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductFormUiState())
    val state: StateFlow<ProductFormUiState> = _state.asStateFlow()

    init {
        observeCategories()
            .onEach { categories ->
                _state.update { current ->
                    current.copy(
                        categories = categories,
                        // Default to "Otros" until the user picks one.
                        selectedCategory = current.selectedCategory
                            ?: categories.firstOrNull { it.name == DEFAULT_CATEGORY }
                            ?: categories.firstOrNull(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onCategorySelected(category: Category) =
        _state.update { it.copy(selectedCategory = category) }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            val path = imageStore.saveProductImage(uri)
            _state.update { it.copy(imagePath = path) }
        }
    }

    /** Prepare a destination for a camera capture (the screen launches the camera with its uri). */
    fun newCameraTarget(): CameraTarget = imageStore.newCameraTarget()

    /** The camera saved a photo at [path]; use it as the product image. */
    fun onPhotoTaken(path: String) = _state.update { it.copy(imagePath = path) }

    fun onErrorShown() = _state.update { it.copy(error = null) }

    fun save(
        name: String,
        costText: String,
        pvpText: String,
        stockText: String,
        sku: String,
        description: String,
    ) {
        val cost = Money.fromPesosOrNull(costText)
        val pvp = Money.fromPesosOrNull(pvpText)
        val validationError = when {
            name.isBlank() -> "El nombre es obligatorio"
            cost == null || cost.isNegative -> "Introduce un coste válido"
            pvp == null || pvp.isNegative -> "Introduce un PVP válido"
            else -> null
        }
        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        val stock = stockText.trim().toIntOrNull()?.takeIf { it >= 0 } ?: 0
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            runCatching {
                createProduct(
                    NewProduct(
                        name = name,
                        cost = cost!!,
                        pvp = pvp!!,
                        stock = Quantity(stock),
                        sku = sku.ifBlank { null },
                        categoryId = _state.value.selectedCategory?.id,
                        description = description.ifBlank { null },
                        imagePath = _state.value.imagePath,
                    ),
                )
            }.onSuccess {
                _state.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message ?: "No se pudo guardar") }
            }
        }
    }

    private companion object {
        const val DEFAULT_CATEGORY = "Otros"
    }
}
