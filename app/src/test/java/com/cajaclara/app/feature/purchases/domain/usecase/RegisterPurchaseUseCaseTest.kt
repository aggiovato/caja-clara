package com.cajaclara.app.feature.purchases.domain.usecase

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.assertThrowsOf
import com.cajaclara.app.feature.products.domain.usecase.sampleProduct
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.purchases.domain.model.Purchase
import com.cajaclara.app.feature.purchases.domain.model.PurchaseLine
import com.cajaclara.app.feature.purchases.domain.repository.PurchasesRepository
import com.cajaclara.app.feature.purchases.domain.valueobject.PurchaseId
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

private class FakePurchasesRepository : PurchasesRepository {
    val purchases = mutableListOf<Purchase>()
    val lines = mutableListOf<PurchaseLine>()
    private var nextId = 1L
    override suspend fun registerPurchase(purchase: Purchase, lines: List<PurchaseLine>): PurchaseId {
        val id = PurchaseId(nextId++)
        purchases.add(purchase.copy(id = id))
        this.lines.addAll(lines.map { it.copy(purchaseId = id) })
        return id
    }
    override fun observeDailyPurchases(date: LocalDate): Flow<List<Purchase>> = flowOf(purchases)
    override fun observePurchasesBetween(range: DateRange): Flow<List<Purchase>> = flowOf(purchases)
}

private class RecordingStockRepository : StockRepository {
    val movements = mutableListOf<StockMovement>()
    override suspend fun record(movement: StockMovement) { movements.add(movement) }
    override fun observeMovements(productId: ProductId) = flowOf(movements)
}

class RegisterPurchaseUseCaseTest {

    private val clock = Clock.fixed(Instant.parse("2026-06-30T10:00:00Z"), ZoneOffset.UTC)
    private val products = FakeProductRepository()
    private val purchases = FakePurchasesRepository()
    private val stock = RecordingStockRepository()
    private val register = RegisterPurchaseUseCase(purchases, products, stock, clock)

    @Test
    fun `raises stock, records IN movement and registers investment`() = runTest {
        val id = products.seed(sampleProduct(name = "Café", cost = Money.fromPesos("2,10"), stock = Quantity(5)))

        register(listOf(PurchaseItem(id, Quantity(10), Money.fromPesos("2,30"))))

        assertEquals(Quantity(15), products.getProduct(id)!!.stockQuantity)
        val movement = stock.movements.single()
        assertEquals(StockMovementType.IN, movement.type)
        assertEquals(Quantity(10), movement.quantity)
        assertEquals(Money.fromPesos("23,00"), purchases.purchases.single().totalInvestment) // 10 * 2,30
    }

    @Test
    fun `updates product cost and appends price history when requested`() = runTest {
        val id = products.seed(sampleProduct(cost = Money.fromPesos("2,10"), pvp = Money.fromPesos("3,50")))

        register(listOf(PurchaseItem(id, Quantity(4), Money.fromPesos("2,30"), updateProductCost = true)))

        assertEquals(Money.fromPesos("2,30"), products.getProduct(id)!!.currentCost)
        val history = products.priceHistory.single()
        assertEquals(Money.fromPesos("2,10"), history.oldCost)
        assertEquals(Money.fromPesos("2,30"), history.newCost)
        assertEquals(Money.fromPesos("3,50"), history.newPvp) // pvp unchanged
    }

    @Test
    fun `keeps cost when updateProductCost is false`() = runTest {
        val id = products.seed(sampleProduct(cost = Money.fromPesos("2,10")))
        register(listOf(PurchaseItem(id, Quantity(4), Money.fromPesos("2,30"), updateProductCost = false)))
        assertEquals(Money.fromPesos("2,10"), products.getProduct(id)!!.currentCost)
        assertEquals(0, products.priceHistory.size)
    }

    @Test
    fun `restocking a sold-out product reactivates it`() = runTest {
        val id = products.seed(sampleProduct(stock = Quantity.ZERO, status = ProductStatus.SOLD_OUT))
        register(listOf(PurchaseItem(id, Quantity(6), Money.fromPesos("1,00"))))
        assertEquals(ProductStatus.ACTIVE, products.getProduct(id)!!.status)
    }

    @Test
    fun `multiple lines sum the investment`() = runTest {
        val a = products.seed(sampleProduct(name = "A", stock = Quantity(0)))
        val b = products.seed(sampleProduct(name = "B", stock = Quantity(0)))
        register(
            listOf(
                PurchaseItem(a, Quantity(3), Money.fromPesos("1,00")),
                PurchaseItem(b, Quantity(2), Money.fromPesos("2,50")),
            ),
        )
        assertEquals(Money.fromPesos("8,00"), purchases.purchases.single().totalInvestment) // 3 + 5
        assertEquals(2, stock.movements.size)
    }

    @Test
    fun `rejects empty list`() = runTest {
        assertThrowsOf<IllegalArgumentException> { register(emptyList()) }
    }
}
