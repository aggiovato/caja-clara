package com.cajaclara.app.feature.products.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cajaclara.app.feature.products.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insert(entity: CategoryEntity): Long

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
