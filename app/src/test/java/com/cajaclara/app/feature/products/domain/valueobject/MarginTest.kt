package com.cajaclara.app.feature.products.domain.valueobject

import com.cajaclara.app.core.money.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarginTest {

    private val delta = 0.0001

    @Test
    fun `unit margin is price minus cost`() {
        val m = Margin(cost = Money.fromPesos("4,00"), price = Money.fromPesos("10,00"))
        assertEquals(Money(600), m.unitMargin)
    }

    @Test
    fun `unit margin is negative when price is below cost`() {
        val m = Margin(cost = Money.fromPesos("10,00"), price = Money.fromPesos("6,00"))
        assertEquals(Money(-400), m.unitMargin)
    }

    @Test
    fun `percent on price`() {
        val m = Margin(cost = Money.fromPesos("4,00"), price = Money.fromPesos("10,00"))
        assertEquals(60.0, m.percentOnPrice!!, delta)
    }

    @Test
    fun `markup on cost`() {
        val m = Margin(cost = Money.fromPesos("4,00"), price = Money.fromPesos("10,00"))
        assertEquals(150.0, m.markupOnCost!!, delta)
    }

    @Test
    fun `negative percentages on a loss`() {
        val m = Margin(cost = Money.fromPesos("10,00"), price = Money.fromPesos("6,00"))
        assertEquals(-66.6667, m.percentOnPrice!!, delta)
        assertEquals(-40.0, m.markupOnCost!!, delta)
    }

    @Test
    fun `percent on price is null when price is zero`() {
        val m = Margin(cost = Money.fromPesos("4,00"), price = Money.ZERO)
        assertNull(m.percentOnPrice)
        assertEquals(-100.0, m.markupOnCost!!, delta)
    }

    @Test
    fun `markup is null when cost is zero`() {
        val m = Margin(cost = Money.ZERO, price = Money.fromPesos("10,00"))
        assertNull(m.markupOnCost)
        assertEquals(100.0, m.percentOnPrice!!, delta)
    }

    @Test
    fun `both zero gives zero margin and null percentages`() {
        val m = Margin(cost = Money.ZERO, price = Money.ZERO)
        assertEquals(Money.ZERO, m.unitMargin)
        assertNull(m.percentOnPrice)
        assertNull(m.markupOnCost)
        assertTrue(m.isBreakEven)
    }

    @Test
    fun `classifies profit`() {
        val m = Margin(cost = Money.fromPesos("4,00"), price = Money.fromPesos("10,00"))
        assertTrue(m.isProfit)
        assertFalse(m.isLoss)
        assertFalse(m.isBreakEven)
        assertFalse(m.isBelowCost)
    }

    @Test
    fun `classifies loss and warns about price below cost`() {
        val m = Margin(cost = Money.fromPesos("10,00"), price = Money.fromPesos("6,00"))
        assertTrue(m.isLoss)
        assertTrue(m.isBelowCost)
        assertFalse(m.isProfit)
    }

    @Test
    fun `classifies break-even when cost equals price`() {
        val m = Margin(cost = Money.fromPesos("5,00"), price = Money.fromPesos("5,00"))
        assertTrue(m.isBreakEven)
        assertEquals(0.0, m.percentOnPrice!!, delta)
        assertEquals(0.0, m.markupOnCost!!, delta)
        assertFalse(m.isBelowCost)
    }

    @Test
    fun `estimated profit for stock multiplies the unit margin`() {
        val m = Margin(cost = Money.fromPesos("4,00"), price = Money.fromPesos("10,00"))
        assertEquals(Money(3000), m.estimatedProfitForStock(5))
    }

    @Test
    fun `estimated profit for stock is zero with no units`() {
        val m = Margin(cost = Money.fromPesos("4,00"), price = Money.fromPesos("10,00"))
        assertEquals(Money.ZERO, m.estimatedProfitForStock(0))
    }
}
