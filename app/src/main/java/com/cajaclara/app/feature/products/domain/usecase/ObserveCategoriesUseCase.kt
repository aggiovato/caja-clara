package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

/** Observes the available categories (for the product form's picker). */
class ObserveCategoriesUseCase(
    private val repository: CategoryRepository,
) {
    operator fun invoke(): Flow<List<Category>> = repository.observeCategories()
}
