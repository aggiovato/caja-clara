package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class CreateProductUseCaseTest {

    private val now = Instant.parse("2026-06-28T10:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val repo = FakeProductRepository()
    private val create = CreateProductUseCase(repo, clock)

    @Test
    fun `creates an active product with timestamps and trimmed name`() = runTest {
        val id = create(
            NewProduct(
                name = "  Coffee  ",
                cost = Money.fromPesos("4,00"),
                pvp = Money.fromPesos("10,00"),
                stock = Quantity(5),
            ),
        )
        val p = repo.getProduct(id)!!
        assertEquals("Coffee", p.name)
        assertEquals(ProductStatus.ACTIVE, p.status)
        assertEquals(now, p.createdAt)
        assertEquals(now, p.updatedAt)
        assertEquals(Quantity(5), p.stockQuantity)
    }

    @Test
    fun `blank optional fields become null`() = runTest {
        val id = create(
            NewProduct(name = "X", cost = Money.ZERO, pvp = Money.ZERO, sku = "  ", description = "  "),
        )
        val p = repo.getProduct(id)!!
        assertEquals(null, p.sku)
        assertEquals(null, p.description)
    }

    @Test
    fun `rejects a blank name`() = runTest {
        assertThrowsOf<IllegalArgumentException> {
            create(NewProduct(name = "   ", cost = Money.ZERO, pvp = Money.ZERO))
        }
    }

    @Test
    fun `rejects negative cost or price`() = runTest {
        assertThrowsOf<IllegalArgumentException> {
            create(NewProduct(name = "X", cost = Money(-1), pvp = Money.ZERO))
        }
        assertThrowsOf<IllegalArgumentException> {
            create(NewProduct(name = "X", cost = Money.ZERO, pvp = Money(-1)))
        }
    }

    @Test
    fun `allows price below cost`() = runTest {
        val id = create(
            NewProduct(name = "X", cost = Money.fromPesos("10,00"), pvp = Money.fromPesos("6,00")),
        )
        assertTrue(repo.getProduct(id)!!.margin.isBelowCost)
    }
}
