package com.cajaclara.app.feature.products.domain.repository

import com.cajaclara.app.feature.products.domain.model.Category
import kotlinx.coroutines.flow.Flow

/** Port for category persistence. Implemented by Room in the data layer. */
interface CategoryRepository {

    /** Observe all categories, ordered by name. */
    fun observeCategories(): Flow<List<Category>>
}
