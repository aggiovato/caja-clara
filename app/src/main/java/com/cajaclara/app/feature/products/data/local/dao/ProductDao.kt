package com.cajaclara.app.feature.products.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cajaclara.app.feature.products.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    /**
     * Observe products, optionally filtered by [status] (null = any) and a free-text
     * [query] over name/SKU (null/blank = no search). Covers all `ProductFilter` cases.
     */
    @Query(
        """
        SELECT * FROM products
        WHERE status != 'ARCHIVED'
          AND (:status IS NULL OR status = :status)
          AND (:query IS NULL OR name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%')
        ORDER BY name ASC
        """,
    )
    fun observeFiltered(status: String?, query: String?): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun findById(id: Long): ProductEntity?

    @Insert
    suspend fun insert(entity: ProductEntity): Long

    @Update
    suspend fun update(entity: ProductEntity)
}
