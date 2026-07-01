package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class UpdateProductPvpUseCaseTest {

    private val now = Instant.parse("2026-06-28T10:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val repo = FakeProductRepository()
    private val updatePvp = UpdateProductPvpUseCase(repo, FakeSettingsRepository(), clock)

    private fun seed(cost: Money, pvp: Money): ProductId =
        repo.seed(sampleProduct(cost = cost, pvp = pvp))

    @Test
    fun `updates price and writes history when changed`() = runTest {
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))

        updatePvp(id, Money.fromPesos("12,00"))

        assertEquals(Money.fromPesos("12,00"), repo.getProduct(id)!!.currentPvp)
        val h = repo.priceHistory.single()
        assertEquals(Money.fromPesos("4,00"), h.oldCost)
        assertEquals(Money.fromPesos("4,00"), h.newCost)
        assertEquals(Money.fromPesos("10,00"), h.oldPvp)
        assertEquals(Money.fromPesos("12,00"), h.newPvp)
    }

    @Test
    fun `allows a price below cost without throwing`() = runTest {
        val id = seed(cost = Money.fromPesos("10,00"), pvp = Money.fromPesos("10,00"))

        updatePvp(id, Money.fromPesos("6,00")) // below cost, allowed

        val p = repo.getProduct(id)!!
        assertEquals(Money.fromPesos("6,00"), p.currentPvp)
        assertTrue(p.margin.isBelowCost)
        assertEquals(1, repo.priceHistory.size)
    }

    @Test
    fun `is a no-op when the price does not change`() = runTest {
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))

        updatePvp(id, Money.fromPesos("10,00"))

        assertEquals(0, repo.priceHistory.size)
        assertEquals(Instant.EPOCH, repo.getProduct(id)!!.updatedAt)
    }

    @Test
    fun `rejects a negative price`() = runTest {
        val id = seed(cost = Money.fromPesos("4,00"), pvp = Money.fromPesos("10,00"))
        assertThrowsOf<IllegalArgumentException> { updatePvp(id, Money(-1)) }
    }
}
