package com.cajaclara.app.di

import android.content.Context
import androidx.room.Room
import com.cajaclara.app.database.AppDatabase
import com.cajaclara.app.feature.products.data.local.dao.PriceHistoryDao
import com.cajaclara.app.feature.products.data.local.dao.ProductDao
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
        Room.databaseBuilder(context, AppDatabase::class.java, "caja_clara.db").build()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun providePriceHistoryDao(db: AppDatabase): PriceHistoryDao = db.priceHistoryDao()
}
