package com.cajaclara.app.ui.home.home

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.sampleProduct
import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import com.cajaclara.app.feature.sales.domain.usecase.ObserveCashCloseUseCase
import com.cajaclara.app.feature.stats.domain.model.DailyBalance
import com.cajaclara.app.feature.stats.domain.model.DailyCashPoint
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import com.cajaclara.app.feature.stats.domain.model.ProductPricePoint
import com.cajaclara.app.feature.stats.domain.repository.AnalyticsRepository
import com.cajaclara.app.feature.stats.domain.usecase.ObserveAccountBalanceUseCase
import com.cajaclara.app.feature.stats.domain.usecase.ObserveDailyBalanceUseCase
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private class FakeAnalyticsRepository(private val balance: Money) : AnalyticsRepository {
    override fun observeDailyBalance(date: LocalDate): Flow<DailyBalance> =
        flowOf(DailyBalance(date, Money.fromPesos("40,00"), Money.fromPesos("16,00"), Money.ZERO, 3, 5))
    override fun observeSalesEvolution(range: DateRange): Flow<List<DailySalesPoint>> = flowOf(emptyList())
    override fun observeCashFlow(range: DateRange): Flow<List<DailyCashPoint>> = flowOf(emptyList())
    override fun observeAccountBalance(): Flow<Money> = flowOf(balance)
    override fun observeProductPriceEvolution(productId: ProductId): Flow<List<ProductPricePoint>> = flowOf(emptyList())
}

private class FakeCashCloseRepository(private val close: CashClose?) : CashCloseRepository {
    override fun observeClose(date: LocalDate): Flow<CashClose?> = flowOf(close)
    override suspend fun save(close: CashClose) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private val clock = Clock.fixed(Instant.parse("2026-06-30T10:00:00Z"), ZoneOffset.UTC)

    private fun viewModel(products: List<Product>, balance: Money = Money.fromPesos("100,00"), close: CashClose? = null): HomeViewModel {
        val repo = FakeProductRepository()
        products.forEach { repo.seed(it) }
        return HomeViewModel(
            observeAccountBalance = ObserveAccountBalanceUseCase(FakeAnalyticsRepository(balance)),
            observeDailyBalance = ObserveDailyBalanceUseCase(FakeAnalyticsRepository(balance)),
            observeCashClose = ObserveCashCloseUseCase(FakeCashCloseRepository(close)),
            observeProducts = ObserveProductsUseCase(repo),
            clock = clock,
        )
    }

    @Test
    fun `counts sold out and low stock, exposes balance`() = runTest {
        val products = listOf(
            sampleProduct(name = "A", stock = Quantity(0), status = ProductStatus.SOLD_OUT),
            sampleProduct(name = "B", stock = Quantity(3), status = ProductStatus.ACTIVE), // low
            sampleProduct(name = "C", stock = Quantity(5), status = ProductStatus.ACTIVE), // low (boundary)
            sampleProduct(name = "D", stock = Quantity(40), status = ProductStatus.ACTIVE), // ok
        )
        val state = viewModel(products, balance = Money.fromPesos("1.250,00")).state.first { !it.isLoading }

        assertEquals(Money.fromPesos("1.250,00"), state.accountBalance)
        assertEquals(1, state.soldOutCount)
        assertEquals(2, state.lowStockCount)
        assertEquals(true, state.hasStockAlerts)
    }

    @Test
    fun `no alerts when stock is healthy`() = runTest {
        val products = listOf(sampleProduct(name = "A", stock = Quantity(40), status = ProductStatus.ACTIVE))
        val state = viewModel(products).state.first { !it.isLoading }
        assertEquals(0, state.soldOutCount)
        assertEquals(0, state.lowStockCount)
        assertEquals(false, state.hasStockAlerts)
    }
}
