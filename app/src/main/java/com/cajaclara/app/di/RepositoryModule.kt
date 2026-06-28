package com.cajaclara.app.di

import com.cajaclara.app.feature.products.data.repository.RoomProductRepository
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
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
}
