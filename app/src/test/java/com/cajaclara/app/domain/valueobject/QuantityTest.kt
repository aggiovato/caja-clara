package com.cajaclara.app.domain.valueobject

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class QuantityTest {

    @Test
    fun `wraps a non-negative value`() {
        assertEquals(5, Quantity(5).value)
        assertEquals(0, Quantity.ZERO.value)
    }

    @Test
    fun `rejects negative values`() {
        assertThrows(IllegalArgumentException::class.java) { Quantity(-1) }
    }

    @Test
    fun `addition and subtraction`() {
        assertEquals(Quantity(8), Quantity(5) + Quantity(3))
        assertEquals(Quantity(2), Quantity(5) - Quantity(3))
    }

    @Test
    fun `subtracting below zero throws (cannot remove more than exists)`() {
        assertThrows(IllegalArgumentException::class.java) { Quantity(3) - Quantity(5) }
    }

    @Test
    fun predicates() {
        assertTrue(Quantity.ZERO.isZero)
        assertFalse(Quantity.ZERO.isPositive)
        assertTrue(Quantity(1).isPositive)
    }

    @Test
    fun `is comparable`() {
        assertTrue(Quantity(2) < Quantity(5))
        assertEquals(0, Quantity(4).compareTo(Quantity(4)))
    }
}
