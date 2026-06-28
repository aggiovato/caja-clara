package com.cajaclara.app.feature.products.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductStatusTest {

    @Test
    fun `only an active product is sellable`() {
        assertTrue(ProductStatus.ACTIVE.isSellable)
        assertFalse(ProductStatus.SOLD_OUT.isSellable)
        assertFalse(ProductStatus.PAUSED.isSellable)
    }

    @Test
    fun `status values`() {
        assertEquals(
            listOf("ACTIVE", "SOLD_OUT", "PAUSED"),
            ProductStatus.entries.map { it.name },
        )
    }
}
