package com.cajaclara.app.di

import android.content.Context
import androidx.room.Room
import com.cajaclara.app.database.AppDatabase
import com.cajaclara.app.database.SeedCallback
import com.cajaclara.app.feature.products.data.local.dao.CategoryDao
import com.cajaclara.app.feature.products.data.local.dao.PriceHistoryDao
import com.cajaclara.app.feature.products.data.local.dao.ProductDao
import com.cajaclara.app.feature.sales.data.local.dao.CashCloseDao
import com.cajaclara.app.feature.sales.data.local.dao.SaleDao
import com.cajaclara.app.feature.stock.data.local.dao.StockMovementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides the Room database and the feature DAOs. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "caja_clara.db")
            // Pre-release: no real installs, schema still in flux. Instead of writing a
            // migration for every change, wipe and let the seed repopulate. Re-introduce
            // real migrations (and drop this) before the first shipped release.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(SeedCallback)
            .build()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun providePriceHistoryDao(db: AppDatabase): PriceHistoryDao = db.priceHistoryDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideStockMovementDao(db: AppDatabase): StockMovementDao = db.stockMovementDao()

    @Provides
    fun provideSaleDao(db: AppDatabase): SaleDao = db.saleDao()

    @Provides
    fun provideCashCloseDao(db: AppDatabase): CashCloseDao = db.cashCloseDao()
}
