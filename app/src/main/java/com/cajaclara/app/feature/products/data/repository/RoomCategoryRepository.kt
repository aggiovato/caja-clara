package com.cajaclara.app.feature.products.data.repository

import com.cajaclara.app.feature.products.data.local.dao.CategoryDao
import com.cajaclara.app.feature.products.data.mapper.toDomain
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room-backed [CategoryRepository]. */
class RoomCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { rows -> rows.map { it.toDomain() } }
}
