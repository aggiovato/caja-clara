package com.cajaclara.app.feature.sales.domain.usecase

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.assertThrowsOf
import com.cajaclara.app.feature.products.domain.usecase.sampleProduct
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.model.SaleLine
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.model.StockMovementType
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private class FakeSalesRepository : SalesRepository {
    val sales = mutableListOf<Sale>()
    val lines = mutableListOf<SaleLine>()
    private var nextId = 1L
    override suspend fun registerSale(sale: Sale, lines: List<SaleLine>): SaleId {
        val id = SaleId(nextId++)
        sales.add(sale.copy(id = id))
        this.lines.addAll(lines.map { it.copy(saleId = id) })
        return id
    }
    override fun observeDailySales(date: LocalDate): Flow<List<Sale>> = flowOf(sales)
    override fun observeSalesBetween(range: DateRange): Flow<List<Sale>> = flowOf(sales)
}

private class RecordingStockRepository : StockRepository {
    val movements = mutableListOf<StockMovement>()
    override suspend fun record(movement: StockMovement) { movements.add(movement) }
    override fun observeMovements(productId: com.cajaclara.app.feature.products.domain.valueobject.ProductId) =
        flowOf(movements)
}

class RegisterSaleUseCaseTest {

    private val clock = Clock.fixed(Instant.parse("2026-06-29T12:00:00Z"), ZoneOffset.UTC)
    private val products = FakeProductRepository()
    private val sales = FakeSalesRepository()
    private val stock = RecordingStockRepository()
    private val register = RegisterSaleUseCase(sales, products, stock, clock)

    @Test
    fun `registers sale with snapshot totals and deducts stock`() = runTest {
        val id = products.seed(
            sampleProduct(name = "Café", cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"), stock = Quantity(5)),
        )

        register(listOf(SaleItem(id, Quantity(2))))

        val sale = sales.sales.single()
        assertEquals(Money.fromPesos("20,00"), sale.totalRevenue)
        assertEquals(Money.fromPesos("8,00"), sale.totalCost)
        assertEquals(Money.fromPesos("12,00"), sale.totalProfit)

        val line = sales.lines.single()
        assertEquals("Café", line.productNameSnapshot)
        assertEquals(Quantity(2), line.quantity)

        assertEquals(Quantity(3), products.getProduct(id)!!.stockQuantity)
        val movement = stock.movements.single()
        assertEquals(StockMovementType.OUT, movement.type)
        assertEquals(Quantity(2), movement.quantity)
    }

    @Test
    fun `selling the last units marks the product sold out`() = runTest {
        val id = products.seed(sampleProduct(stock = Quantity(3)))
        register(listOf(SaleItem(id, Quantity(3))))
        assertEquals(ProductStatus.SOLD_OUT, products.getProduct(id)!!.status)
        assertEquals(Quantity.ZERO, products.getProduct(id)!!.stockQuantity)
    }

    @Test
    fun `rejects selling more than available`() = runTest {
        val id = products.seed(sampleProduct(stock = Quantity(2)))
        assertThrowsOf<InsufficientStockException> { register(listOf(SaleItem(id, Quantity(3)))) }
        // Nothing persisted on failure.
        assertEquals(0, sales.sales.size)
    }

    @Test
    fun `rejects a non-active product`() = runTest {
        val id = products.seed(sampleProduct(status = ProductStatus.PAUSED))
        assertThrowsOf<IllegalStateException> { register(listOf(SaleItem(id, Quantity(1)))) }
    }

    @Test
    fun `rejects an empty cart`() = runTest {
        assertThrowsOf<IllegalArgumentException> { register(emptyList()) }
    }

    @Test
    fun `totals sum across multiple lines`() = runTest {
        val a = products.seed(sampleProduct(name = "A", cost = Money.fromPesos("1,00"), pvp = Money.fromPesos("3,00"), stock = Quantity(10)))
        val b = products.seed(sampleProduct(name = "B", cost = Money.fromPesos("2,00"), pvp = Money.fromPesos("5,00"), stock = Quantity(10)))

        register(listOf(SaleItem(a, Quantity(2)), SaleItem(b, Quantity(1))))

        val sale = sales.sales.single()
        assertEquals(Money.fromPesos("11,00"), sale.totalRevenue) // 2*3 + 1*5
        assertEquals(Money.fromPesos("4,00"), sale.totalCost) // 2*1 + 1*2
        assertEquals(2, stock.movements.size)
    }
}
