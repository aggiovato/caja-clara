package com.cajaclara.app.di

import com.cajaclara.app.feature.products.data.AndroidImageStore
import com.cajaclara.app.feature.products.data.ImageStore
import com.cajaclara.app.feature.products.data.repository.RoomCategoryRepository
import com.cajaclara.app.feature.products.data.repository.RoomProductRepository
import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.sales.data.repository.RoomCashCloseRepository
import com.cajaclara.app.feature.sales.data.repository.RoomSalesRepository
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import com.cajaclara.app.feature.stock.data.repository.RoomStockRepository
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds domain repository ports to their data-layer implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: RoomProductRepository): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: RoomCategoryRepository): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindImageStore(impl: AndroidImageStore): ImageStore

    @Binds
    @Singleton
    abstract fun bindStockRepository(impl: RoomStockRepository): StockRepository

    @Binds
    @Singleton
    abstract fun bindSalesRepository(impl: RoomSalesRepository): SalesRepository

    @Binds
    @Singleton
    abstract fun bindCashCloseRepository(impl: RoomCashCloseRepository): CashCloseRepository
}
