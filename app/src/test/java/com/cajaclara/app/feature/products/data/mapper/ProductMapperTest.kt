package com.cajaclara.app.feature.products.data.mapper

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.data.local.entity.ProductEntity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class ProductMapperTest {

    private val product = Product(
        id = ProductId(7),
        name = "Coffee",
        sku = "CAF-01",
        categoryId = CategoryId(3),
        description = "Ground coffee",
        currentCost = Money.fromPesos("4,00"),
        currentPvp = Money.fromPesos("10,00"),
        stockQuantity = Quantity(5),
        status = ProductStatus.SOLD_OUT,
        createdAt = Instant.ofEpochMilli(1_000),
        updatedAt = Instant.ofEpochMilli(2_000),
    )

    @Test
    fun `entity columns reflect the domain values`() {
        val e = product.toEntity()
        assertEquals(7L, e.id)
        assertEquals(400L, e.currentCostCents)
        assertEquals(1000L, e.currentPvpCents)
        assertEquals(5, e.stockQuantity)
        assertEquals("SOLD_OUT", e.status)
        assertEquals(3L, e.categoryId)
        assertEquals(1_000L, e.createdAt)
    }

    @Test
    fun `round-trips domain to entity and back`() {
        assertEquals(product, product.toEntity().toDomain())
    }

    @Test
    fun `unsaved id maps to zero for autogeneration`() {
        assertEquals(0L, product.copy(id = ProductId.UNSAVED).toEntity().id)
    }

    @Test
    fun `null category and optional fields survive the round trip`() {
        val minimal = product.copy(categoryId = null, sku = null, description = null)
        val back = minimal.toEntity().toDomain()
        assertNull(back.categoryId)
        assertNull(back.sku)
        assertNull(back.description)
    }

    @Test
    fun `entity with unknown status would fail fast`() {
        val bad = ProductEntity(
            id = 1, name = "X", sku = null, categoryId = null, description = null,
            currentCostCents = 0, currentPvpCents = 0, stockQuantity = 0,
            status = "NOT_A_STATUS", createdAt = 0, updatedAt = 0,
        )
        try {
            bad.toDomain()
            throw AssertionError("Expected IllegalArgumentException")
        } catch (_: IllegalArgumentException) {
            // ProductStatus.valueOf rejects unknown values
        }
    }
}
