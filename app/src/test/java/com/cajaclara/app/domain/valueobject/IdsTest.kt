package com.cajaclara.app.domain.valueobject

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IdsTest {

    @Test
    fun `UNSAVED wraps zero`() {
        assertEquals(0L, ProductId.UNSAVED.value)
        assertEquals(0L, CategoryId.UNSAVED.value)
        assertEquals(0L, SaleId.UNSAVED.value)
    }

    @Test
    fun `isSaved is false only for the unsaved sentinel`() {
        assertFalse(ProductId.UNSAVED.isSaved)
        assertTrue(ProductId(1).isSaved)

        assertFalse(CategoryId.UNSAVED.isSaved)
        assertTrue(CategoryId(7).isSaved)

        assertFalse(SaleId.UNSAVED.isSaved)
        assertTrue(SaleId(42).isSaved)
    }

    @Test
    fun `equality is by wrapped value`() {
        assertEquals(ProductId(3), ProductId(3))
        assertEquals(SaleId(3), SaleId(3))
    }
}
