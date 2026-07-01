package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class UpdateProductCostUseCaseTest {

    private val now = Instant.parse("2026-06-28T10:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val repo = FakeProductRepository()
    private val updateCost = UpdateProductCostUseCase(repo, FakeSettingsRepository(), clock)

    private fun seed(cost: Money, pvp: Money): ProductId =
        repo.seed(sampleProduct(cost = cost, pvp = pvp))

    @Test
    fun `updates cost and writes history when changed`() = runTest {
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))

        updateCost(id, Money.fromPesos("5,00"), reason = "supplier")

        val p = repo.getProduct(id)!!
        assertEquals(Money.fromPesos("5,00"), p.currentCost)
        assertEquals(now, p.updatedAt)

        val h = repo.priceHistory.single()
        assertEquals(Money.fromPesos("4,00"), h.oldCost)
        assertEquals(Money.fromPesos("5,00"), h.newCost)
        assertEquals(Money.fromPesos("10,00"), h.oldPvp)
        assertEquals(Money.fromPesos("10,00"), h.newPvp)
        assertEquals("supplier", h.reason)
    }

    @Test
    fun `is a no-op when the cost does not change`() = runTest {
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))

        updateCost(id, Money.fromPesos("4,00"))

        assertEquals(0, repo.priceHistory.size)
        assertEquals(Instant.EPOCH, repo.getProduct(id)!!.updatedAt) // untouched
    }

    @Test
    fun `rejects a negative cost`() = runTest {
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))
        assertThrowsOf<IllegalArgumentException> { updateCost(id, Money(-1)) }
    }

    @Test
    fun `throws when the product does not exist`() = runTest {
        assertThrowsOf<NoSuchElementException> {
            updateCost(ProductId(999), Money.fromPesos("5,00"))
        }
    }

    @Test
    fun `rejects a cost that breaks the minimum margin`() = runTest {
        // Min margin 40% over price. PVP 10,00 -> cost must keep margin >= 40% (cost <= 6,00).
        val useCase = UpdateProductCostUseCase(repo, FakeSettingsRepository(minMarginPercent = 40.0), clock)
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))

        // 8,00 cost -> margin 20% < 40% -> rejected, nothing written.
        assertThrowsOf<MinMarginViolationException> { useCase(id, Money.fromPesos("8,00")) }
        assertEquals(0, repo.priceHistory.size)
        assertEquals(Money.fromPesos("4,00"), repo.getProduct(id)!!.currentCost)
    }

    @Test
    fun `allows a cost that keeps the minimum margin`() = runTest {
        val useCase = UpdateProductCostUseCase(repo, FakeSettingsRepository(minMarginPercent = 40.0), clock)
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))

        useCase(id, Money.fromPesos("5,00")) // margin 50% >= 40%
        assertEquals(Money.fromPesos("5,00"), repo.getProduct(id)!!.currentCost)
    }
}
