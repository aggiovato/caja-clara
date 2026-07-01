package com.cajaclara.app.ui.products.productform

import android.net.Uri
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.ProductId

/** Initial text for the editable fields, applied once to the screen's TextFieldStates. */
data class ProductPrefill(val name: String, val sku: String, val description: String)

/** State for the create/edit product form. Editable text fields live in the screen. */
data class ProductFormUiState(
    val isEdit: Boolean = false,
    val productId: ProductId? = null,
    // Read-only in edit mode (changed via their own actions):
    val currentCost: Money? = null,
    val currentPvp: Money? = null,
    val currentStock: Quantity? = null,
    val status: ProductStatus? = null,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val imagePath: String? = null,
    /** The shop address shown on the shareable image; blank means none configured. */
    val storeAddress: String = "",
    /** One-shot: set to the saved shareable image URI so the screen fires the share intent. */
    val shareImageUri: Uri? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
    /** SKU slug suggested from the name; shown as the SKU field placeholder. */
    val skuSuggestion: String = "",
    /** One-shot: when non-null the screen fills the text fields, then calls onPrefillConsumed(). */
    val prefill: ProductPrefill? = null,
)
