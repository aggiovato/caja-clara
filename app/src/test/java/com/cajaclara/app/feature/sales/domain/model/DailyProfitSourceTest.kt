package com.cajaclara.app.feature.sales.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyProfitSourceTest {

    @Test
    fun `source values`() {
        assertEquals(
            listOf("MANUAL", "SALES_AUTOMATIC"),
            DailyProfitSource.entries.map { it.name },
        )
    }
}
