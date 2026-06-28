package com.cajaclara.app.feature.stock.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class StockMovementTypeTest {

    @Test
    fun `type values`() {
        assertEquals(
            listOf("IN", "OUT", "ADJUSTMENT", "SOLD_OUT", "RESTORED"),
            StockMovementType.entries.map { it.name },
        )
    }
}
