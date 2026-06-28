package com.cajaclara.app.feature.products.data.mapper

import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class CategoryMapperTest {

    private val category = Category(
        id = CategoryId(4),
        name = "Bebidas",
        colorHex = "#2563EB",
        createdAt = Instant.ofEpochMilli(1_500),
    )

    @Test
    fun `round-trips domain to entity and back`() {
        assertEquals(category, category.toEntity().toDomain())
    }

    @Test
    fun `entity columns reflect the domain values`() {
        val e = category.toEntity()
        assertEquals(4L, e.id)
        assertEquals("Bebidas", e.name)
        assertEquals("#2563EB", e.colorHex)
        assertEquals(1_500L, e.createdAt)
    }
}
