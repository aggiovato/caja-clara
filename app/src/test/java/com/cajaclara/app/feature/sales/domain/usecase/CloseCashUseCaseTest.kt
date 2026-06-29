package com.cajaclara.app.feature.sales.domain.usecase

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.model.SaleLine
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private class FakeSalesRepo(private val sales: List<Sale>) : SalesRepository {
    override suspend fun registerSale(sale: Sale, lines: List<SaleLine>): SaleId = SaleId(1)
    override fun observeDailySales(date: LocalDate): Flow<List<Sale>> = flowOf(sales)
    override fun observeSalesBetween(range: DateRange): Flow<List<Sale>> = flowOf(sales)
}

private class FakeCashCloseRepo : CashCloseRepository {
    val saved = mutableListOf<CashClose>()
    override fun observeClose(date: LocalDate): Flow<CashClose?> = flowOf(saved.lastOrNull())
    override suspend fun save(close: CashClose) { saved.add(close) }
}

class CloseCashUseCaseTest {

    private val now = Instant.parse("2026-06-29T18:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)

    private fun sale(revenue: String, cost: String) = Sale(
        id = SaleId(1),
        soldAt = now,
        totalRevenue = Money.fromPesos(revenue),
        totalCost = Money.fromPesos(cost),
        createdAt = now,
    )

    @Test
    fun `snapshots day sales and computes profit and difference`() = runTest {
        val sales = listOf(sale("20,00", "8,00"), sale("5,00", "2,00"))
        val cashRepo = FakeCashCloseRepo()
        val close = CloseCashUseCase(FakeSalesRepo(sales), cashRepo, clock)(Money.fromPesos("25,00"))

        assertEquals(Money.fromPesos("25,00"), close.expectedRevenue)
        assertEquals(Money.fromPesos("10,00"), close.expectedCost)
        assertEquals(Money.fromPesos("15,00"), close.profit)      // revenue - cost
        assertEquals(Money.ZERO, close.difference)                // counted 25 - expected 25
        assertTrue(close.isBalanced)
        assertEquals(2, close.salesCount)
        assertEquals(LocalDate.of(2026, 6, 29), close.date)
        assertEquals(close, cashRepo.saved.single())
    }

    @Test
    fun `shortfall yields a negative difference`() = runTest {
        val close = CloseCashUseCase(FakeSalesRepo(listOf(sale("30,00", "10,00"))), FakeCashCloseRepo(), clock)(
            Money.fromPesos("28,00"),
        )
        assertEquals(Money.fromPesos("-2,00"), close.difference)
    }

    @Test
    fun `surplus yields a positive difference but profit stays sales-based`() = runTest {
        val close = CloseCashUseCase(FakeSalesRepo(listOf(sale("30,00", "10,00"))), FakeCashCloseRepo(), clock)(
            Money.fromPesos("33,00"),
        )
        assertEquals(Money.fromPesos("3,00"), close.difference)
        assertEquals(Money.fromPesos("20,00"), close.profit) // unaffected by surplus
    }

    @Test
    fun `re-closing saves again with the new count`() = runTest {
        val cashRepo = FakeCashCloseRepo()
        val useCase = CloseCashUseCase(FakeSalesRepo(listOf(sale("10,00", "4,00"))), cashRepo, clock)
        useCase(Money.fromPesos("10,00"))
        useCase(Money.fromPesos("12,00"))
        assertEquals(2, cashRepo.saved.size)
        assertEquals(Money.fromPesos("12,00"), cashRepo.saved.last().countedCash)
    }
}
