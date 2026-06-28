package com.cajaclara.app.ui.products.productform

import com.cajaclara.app.feature.products.domain.model.Category

/** State for the create-product form. Text fields are held by the screen as TextFieldStates. */
data class ProductFormUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val imagePath: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)
