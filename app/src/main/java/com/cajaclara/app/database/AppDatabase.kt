package com.cajaclara.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cajaclara.app.feature.products.data.local.dao.CategoryDao
import com.cajaclara.app.feature.products.data.local.dao.PriceHistoryDao
import com.cajaclara.app.feature.products.data.local.dao.ProductDao
import com.cajaclara.app.feature.products.data.local.entity.CategoryEntity
import com.cajaclara.app.feature.products.data.local.entity.PriceHistoryEntity
import com.cajaclara.app.feature.products.data.local.entity.ProductEntity
import com.cajaclara.app.feature.stock.data.local.dao.StockMovementDao
import com.cajaclara.app.feature.stock.data.local.entity.StockMovementEntity

/**
 * The app's single Room database. Each feature contributes its entities and DAOs.
 *
 * Columns are primitive (cents as Long, dates as epoch millis, enums as String); the
 * feature mappers convert to/from the domain, so no Room TypeConverters are needed.
 * exportSchema is off for now; enable it with a schema dir when we start writing
 * migrations.
 */
@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        PriceHistoryEntity::class,
        StockMovementEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun categoryDao(): CategoryDao
    abstract fun stockMovementDao(): StockMovementDao
}
