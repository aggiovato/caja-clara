package com.cajaclara.app.feature.sales.domain.valueobject

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SaleIdTest {

    @Test
    fun `UNSAVED wraps zero`() {
        assertEquals(0L, SaleId.UNSAVED.value)
    }

    @Test
    fun `isSaved is false only for the unsaved sentinel`() {
        assertFalse(SaleId.UNSAVED.isSaved)
        assertTrue(SaleId(42).isSaved)
    }

    @Test
    fun `equality is by wrapped value`() {
        assertEquals(SaleId(3), SaleId(3))
    }
}
