package com.cajaclara.app.ui.sales.sales

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.usecase.sampleProduct
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.model.SaleLine
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import com.cajaclara.app.feature.sales.domain.usecase.CloseCashUseCase
import com.cajaclara.app.feature.sales.domain.usecase.ObserveCashCloseUseCase
import com.cajaclara.app.feature.sales.domain.usecase.ObserveDailySalesUseCase
import com.cajaclara.app.feature.sales.domain.usecase.RegisterSaleUseCase
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

/** A [Clock] whose instant can be moved forward mid-test to simulate the day changing. */
private class MutableClock(
    private var current: Instant,
    private val zone: ZoneId = ZoneOffset.UTC,
) : Clock() {
    fun setInstant(instant: Instant) { current = instant }
    override fun getZone(): ZoneId = zone
    override fun withZone(zone: ZoneId): Clock = MutableClock(current, zone)
    override fun instant(): Instant = current
}

/** In-memory sales repo with per-date flows; records what gets registered. */
private class FakeSalesRepository : SalesRepository {
    private val byDate = mutableMapOf<LocalDate, MutableStateFlow<List<Sale>>>()
    val registered = mutableListOf<Sale>()
    private var nextId = 1L

    private fun flowFor(date: LocalDate) = byDate.getOrPut(date) { MutableStateFlow(emptyList()) }

    override suspend fun registerSale(sale: Sale, lines: List<SaleLine>): SaleId {
        val id = SaleId(nextId++)
        registered.add(sale.copy(id = id))
        return id
    }
    override fun observeDailySales(date: LocalDate): Flow<List<Sale>> = flowFor(date)
    override fun observeSalesBetween(range: DateRange): Flow<List<Sale>> = flowOf(emptyList())
}

/** In-memory cash-close repo with one flow per date. */
private class FakeCashCloseRepository : CashCloseRepository {
    private val byDate = mutableMapOf<LocalDate, MutableStateFlow<CashClose?>>()
    private fun flowFor(date: LocalDate) = byDate.getOrPut(date) { MutableStateFlow(null) }
    fun setClose(date: LocalDate, close: CashClose) { flowFor(date).value = close }
    override fun observeClose(date: LocalDate): Flow<CashClose?> = flowFor(date)
    override suspend fun save(close: CashClose) { flowFor(close.date).value = close }
}

private class RecordingStockRepository : StockRepository {
    override suspend fun record(movement: StockMovement) {}
    override fun observeMovements(productId: ProductId): Flow<List<StockMovement>> = flowOf(emptyList())
}

@OptIn(ExperimentalCoroutinesApi::class)
class SalesViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(
        products: FakeProductRepository,
        sales: FakeSalesRepository,
        cashClose: FakeCashCloseRepository,
        clock: Clock,
    ): SalesViewModel = SalesViewModel(
        observeProducts = ObserveProductsUseCase(products),
        observeDailySales = ObserveDailySalesUseCase(sales),
        observeCashClose = ObserveCashCloseUseCase(cashClose),
        registerSale = RegisterSaleUseCase(sales, products, RecordingStockRepository(), clock),
        closeCashUseCase = CloseCashUseCase(sales, cashClose, clock),
        clock = clock,
    )

    @Test
    fun `pausing a product drops it from the cart and the sale still goes through`() =
        runTest(dispatcher.scheduler) {
            val clock = MutableClock(Instant.parse("2026-06-30T12:00:00Z"))
            val products = FakeProductRepository()
            val aId = products.seed(sampleProduct(name = "A", stock = Quantity(5)))
            val bId = products.seed(sampleProduct(name = "B", stock = Quantity(5)))
            val sales = FakeSalesRepository()
            val vm = viewModel(products, sales, FakeCashCloseRepository(), clock)

            backgroundScope.launch(dispatcher) { vm.state.collect {} }
            runCurrent()

            vm.add(aId.value, available = 5)
            vm.add(bId.value, available = 5)
            runCurrent()
            assertEquals(setOf(aId.value, bId.value), vm.state.value.cart.keys)

            // B is paused: it must leave the cart automatically.
            products.updateProduct(products.getProduct(bId)!!.copy(status = ProductStatus.PAUSED))
            runCurrent()
            assertEquals(setOf(aId.value), vm.state.value.cart.keys)

            // Confirming now sells only the still-active product, with no "product not found" error.
            vm.confirmSale()
            runCurrent()
            assertEquals(null, vm.state.value.error)
            assertEquals(true, vm.state.value.justSold)
            assertEquals(1, sales.registered.size)
        }

    @Test
    fun `daily cash state rolls over when the day changes`() = runTest(dispatcher.scheduler) {
        val clock = MutableClock(Instant.parse("2026-06-30T23:59:00Z"))
        val today = LocalDate.of(2026, 6, 30)
        val cashClose = FakeCashCloseRepository()
        cashClose.setClose(today, sampleClose(today, clock.instant()))
        val vm = viewModel(FakeProductRepository(), FakeSalesRepository(), cashClose, clock)

        backgroundScope.launch(dispatcher) { vm.state.collect {} }
        runCurrent()
        assertEquals(true, vm.state.value.isDayClosed) // 30th is closed

        // Cross midnight: the screen must now track the new (still open) day, not the 30th.
        clock.setInstant(Instant.parse("2026-07-01T00:01:00Z"))
        advanceTimeBy(61_000.milliseconds)
        runCurrent()

        assertEquals(false, vm.state.value.isDayClosed) // 1st has no close yet
    }

    private fun sampleClose(date: LocalDate, closedAt: Instant) = CashClose(
        date = date,
        expectedRevenue = Money.fromPesos("50,00"),
        expectedCost = Money.fromPesos("20,00"),
        countedCash = Money.fromPesos("50,00"),
        salesCount = 2,
        closedAt = closedAt,
        note = null,
    )
}
