package com.cajaclara.app.di

import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.usecase.CreateProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductCostUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductPvpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

/**
 * Provides the product use cases. They are plain domain classes (no DI annotations,
 * keeping the domain framework-free), assembled here from the repository port and Clock.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideObserveProducts(repository: ProductRepository): ObserveProductsUseCase =
        ObserveProductsUseCase(repository)

    @Provides
    fun provideObserveCategories(repository: CategoryRepository): ObserveCategoriesUseCase =
        ObserveCategoriesUseCase(repository)

    @Provides
    fun provideCreateProduct(repository: ProductRepository, clock: Clock): CreateProductUseCase =
        CreateProductUseCase(repository, clock)

    @Provides
    fun provideUpdateProductCost(repository: ProductRepository, clock: Clock): UpdateProductCostUseCase =
        UpdateProductCostUseCase(repository, clock)

    @Provides
    fun provideUpdateProductPvp(repository: ProductRepository, clock: Clock): UpdateProductPvpUseCase =
        UpdateProductPvpUseCase(repository, clock)
}
