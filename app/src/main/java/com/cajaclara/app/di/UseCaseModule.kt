package com.cajaclara.app.di

import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.usecase.ArchiveProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.CreateProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.GetProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.usecase.PauseProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.ResumeProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.SuggestSkuUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductCostUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductPvpUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductUseCase
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import com.cajaclara.app.feature.sales.domain.usecase.CloseCashUseCase
import com.cajaclara.app.feature.sales.domain.usecase.ObserveCashCloseUseCase
import com.cajaclara.app.feature.sales.domain.usecase.ObserveDailySalesUseCase
import com.cajaclara.app.feature.sales.domain.usecase.RegisterSaleUseCase
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import com.cajaclara.app.feature.stats.domain.usecase.ObserveDailyBalanceUseCase
import com.cajaclara.app.feature.stats.domain.usecase.ObserveSalesEvolutionUseCase
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import com.cajaclara.app.feature.stock.domain.usecase.AdjustStockUseCase
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

    @Provides
    fun provideUpdateProduct(repository: ProductRepository, clock: Clock): UpdateProductUseCase =
        UpdateProductUseCase(repository, clock)

    @Provides
    fun providePauseProduct(repository: ProductRepository, clock: Clock): PauseProductUseCase =
        PauseProductUseCase(repository, clock)

    @Provides
    fun provideResumeProduct(repository: ProductRepository, clock: Clock): ResumeProductUseCase =
        ResumeProductUseCase(repository, clock)

    @Provides
    fun provideArchiveProduct(repository: ProductRepository, clock: Clock): ArchiveProductUseCase =
        ArchiveProductUseCase(repository, clock)

    @Provides
    fun provideGetProduct(repository: ProductRepository): GetProductUseCase =
        GetProductUseCase(repository)

    @Provides
    fun provideSuggestSku(): SuggestSkuUseCase = SuggestSkuUseCase()

    @Provides
    fun provideAdjustStock(
        productRepository: ProductRepository,
        stockRepository: StockRepository,
        clock: Clock,
    ): AdjustStockUseCase = AdjustStockUseCase(productRepository, stockRepository, clock)

    @Provides
    fun provideRegisterSale(
        salesRepository: SalesRepository,
        productRepository: ProductRepository,
        stockRepository: StockRepository,
        clock: Clock,
    ): RegisterSaleUseCase = RegisterSaleUseCase(salesRepository, productRepository, stockRepository, clock)

    @Provides
    fun provideObserveDailySales(repository: SalesRepository): ObserveDailySalesUseCase =
        ObserveDailySalesUseCase(repository)

    @Provides
    fun provideCloseCash(
        salesRepository: SalesRepository,
        cashCloseRepository: CashCloseRepository,
        clock: Clock,
    ): CloseCashUseCase = CloseCashUseCase(salesRepository, cashCloseRepository, clock)

    @Provides
    fun provideObserveCashClose(repository: CashCloseRepository): ObserveCashCloseUseCase =
        ObserveCashCloseUseCase(repository)

    @Provides
    fun provideObserveDailyBalance(repository: AnalyticsRepository): ObserveDailyBalanceUseCase =
        ObserveDailyBalanceUseCase(repository)

    @Provides
    fun provideObserveSalesEvolution(repository: AnalyticsRepository): ObserveSalesEvolutionUseCase =
        ObserveSalesEvolutionUseCase(repository)
}
