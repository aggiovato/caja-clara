package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ProductActionsTest {

    private val now = Instant.parse("2026-06-28T10:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val repo = FakeProductRepository()

    private fun seed(status: ProductStatus, stock: Quantity = Quantity(5)): ProductId =
        repo.seed(sampleProduct(status = status, stock = stock))

    @Test
    fun `update changes general fields but keeps cost, pvp and status`() = runTest {
        val id = seed(ProductStatus.ACTIVE)
        val before = repo.getProduct(id)!!

        UpdateProductUseCase(repo, clock)(
            id,
            ProductEdits(name = "  Té  ", sku = "TE-1", categoryId = CategoryId(2), description = "  "),
        )

        val after = repo.getProduct(id)!!
        assertEquals("Té", after.name)
        assertEquals("TE-1", after.sku)
        assertEquals(2L, after.categoryId?.value)
        assertEquals(null, after.description)
        assertEquals(before.currentCost, after.currentCost)
        assertEquals(before.currentPvp, after.currentPvp)
        assertEquals(before.status, after.status)
        assertEquals(now, after.updatedAt)
    }

    @Test
    fun `pause sets PAUSED`() = runTest {
        val id = seed(ProductStatus.ACTIVE)
        PauseProductUseCase(repo, clock)(id)
        assertEquals(ProductStatus.PAUSED, repo.getProduct(id)!!.status)
    }

    @Test
    fun `resume goes active with stock and sold out without`() = runTest {
        val withStock = seed(ProductStatus.PAUSED, stock = Quantity(3))
        val noStock = seed(ProductStatus.PAUSED, stock = Quantity.ZERO)
        ResumeProductUseCase(repo, clock).let { resume ->
            resume(withStock)
            resume(noStock)
        }
        assertEquals(ProductStatus.ACTIVE, repo.getProduct(withStock)!!.status)
        assertEquals(ProductStatus.SOLD_OUT, repo.getProduct(noStock)!!.status)
    }

    @Test
    fun `archive sets ARCHIVED`() = runTest {
        val id = seed(ProductStatus.ACTIVE)
        ArchiveProductUseCase(repo, clock)(id)
        assertEquals(ProductStatus.ARCHIVED, repo.getProduct(id)!!.status)
    }
}
