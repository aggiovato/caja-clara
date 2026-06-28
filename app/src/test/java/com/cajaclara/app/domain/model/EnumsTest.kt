package com.cajaclara.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnumsTest {

    @Test
    fun `only an active product is sellable`() {
        assertTrue(ProductStatus.ACTIVE.isSellable)
        assertFalse(ProductStatus.SOLD_OUT.isSellable)
        assertFalse(ProductStatus.PAUSED.isSellable)
    }

    // The following lock the enum contracts so values are not dropped/reordered by accident.

    @Test
    fun `product status values`() {
        assertEquals(
            listOf("ACTIVE", "SOLD_OUT", "PAUSED"),
            ProductStatus.entries.map { it.name },
        )
    }

    @Test
    fun `stock movement type values`() {
        assertEquals(
            listOf("IN", "OUT", "ADJUSTMENT", "SOLD_OUT", "RESTORED"),
            StockMovementType.entries.map { it.name },
        )
    }

    @Test
    fun `daily profit source values`() {
        assertEquals(
            listOf("MANUAL", "SALES_AUTOMATIC"),
            DailyProfitSource.entries.map { it.name },
        )
    }
}
