package com.cajaclara.app.feature.stock.domain.usecase

import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.sampleProduct
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
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
import java.time.ZoneOffset

private class RecordingStockRepository : StockRepository {
    val movements = mutableListOf<StockMovement>()
    override suspend fun record(movement: StockMovement) { movements.add(movement) }
    override fun observeMovements(productId: ProductId): Flow<List<StockMovement>> = flowOf(movements)
}

class AdjustStockUseCaseTest {

    private val clock = Clock.fixed(Instant.parse("2026-06-29T10:00:00Z"), ZoneOffset.UTC)
    private val productRepo = FakeProductRepository()
    private val stockRepo = RecordingStockRepository()
    private val adjust = AdjustStockUseCase(productRepo, stockRepo, clock)

    private fun seed(stock: Int, status: ProductStatus = ProductStatus.ACTIVE): ProductId =
        productRepo.seed(sampleProduct(stock = Quantity(stock), status = status))

    @Test
    fun `increase records an IN movement and keeps active`() = runTest {
        val id = seed(stock = 10)
        adjust(id, Quantity(25))

        assertEquals(Quantity(25), productRepo.getProduct(id)!!.stockQuantity)
        val m = stockRepo.movements.single()
        assertEquals(StockMovementType.IN, m.type)
        assertEquals(Quantity(15), m.quantity)
        assertEquals(ProductStatus.ACTIVE, productRepo.getProduct(id)!!.status)
    }

    @Test
    fun `decrease without sale records an ADJUSTMENT movement`() = runTest {
        val id = seed(stock = 10)
        adjust(id, Quantity(3))

        val m = stockRepo.movements.single()
        assertEquals(StockMovementType.ADJUSTMENT, m.type)
        assertEquals(Quantity(7), m.quantity)
    }

    @Test
    fun `dropping to zero marks the product sold out`() = runTest {
        val id = seed(stock = 5)
        adjust(id, Quantity.ZERO)
        assertEquals(ProductStatus.SOLD_OUT, productRepo.getProduct(id)!!.status)
    }

    @Test
    fun `adding stock to a sold-out product reactivates it`() = runTest {
        val id = seed(stock = 0, status = ProductStatus.SOLD_OUT)
        adjust(id, Quantity(4))
        assertEquals(ProductStatus.ACTIVE, productRepo.getProduct(id)!!.status)
    }

    @Test
    fun `no change records nothing`() = runTest {
        val id = seed(stock = 10)
        adjust(id, Quantity(10))
        assertEquals(0, stockRepo.movements.size)
    }

    @Test
    fun `paused product keeps its status`() = runTest {
        val id = seed(stock = 5, status = ProductStatus.PAUSED)
        adjust(id, Quantity(8))
        assertEquals(ProductStatus.PAUSED, productRepo.getProduct(id)!!.status)
    }
}
