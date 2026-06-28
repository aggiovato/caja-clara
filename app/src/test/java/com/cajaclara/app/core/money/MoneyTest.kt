package com.cajaclara.app.core.money

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyTest {

    // ---- format() ----

    @Test
    fun `formats a simple amount with comma decimal and CUP code`() {
        assertEquals("12,50 CUP", Money(1250).format())
    }

    @Test
    fun `formats zero`() {
        assertEquals("0,00 CUP", Money.ZERO.format())
    }

    @Test
    fun `pads cents to two digits`() {
        assertEquals("0,05 CUP", Money(5).format())
        assertEquals("0,50 CUP", Money(50).format())
        assertEquals("3,00 CUP", Money(300).format())
    }

    @Test
    fun `groups thousands with a dot`() {
        assertEquals("1.234,56 CUP", Money(123456).format())
        assertEquals("1.000.000,00 CUP", Money(100_000_000).format())
        assertEquals("12.345,00 CUP", Money(1_234_500).format())
    }

    @Test
    fun `formats negatives with a leading sign`() {
        assertEquals("-12,50 CUP", Money(-1250).format())
        assertEquals("-1.234,56 CUP", Money(-123456).format())
    }

    @Test
    fun `toString delegates to format`() {
        assertEquals("7,99 CUP", Money(799).toString())
    }

    // ---- fromPesos(String) ----

    @Test
    fun `parses pesos with comma decimal`() {
        assertEquals(Money(1250), Money.fromPesos("12,50"))
    }

    @Test
    fun `parses pesos with dot decimal`() {
        assertEquals(Money(1250), Money.fromPesos("12.50"))
    }

    @Test
    fun `parses integers without decimals`() {
        assertEquals(Money(1200), Money.fromPesos("12"))
    }

    @Test
    fun `parses a single decimal padding to two`() {
        assertEquals(Money(1250), Money.fromPesos("12,5"))
    }

    @Test
    fun `parses cents without an integer part`() {
        assertEquals(Money(5), Money.fromPesos("0,05"))
        assertEquals(Money(50), Money.fromPesos(",5"))
    }

    @Test
    fun `treats the last separator as decimal and the rest as thousands`() {
        assertEquals(Money(123456), Money.fromPesos("1.234,56"))
        assertEquals(Money(123456), Money.fromPesos("1,234.56"))
    }

    @Test
    fun `tolerates spaces and the currency code`() {
        assertEquals(Money(1250), Money.fromPesos(" 12,5 CUP "))
        assertEquals(Money(1250), Money.fromPesos("12,50 cup"))
        assertEquals(Money(1250), Money.fromPesos("$12,50"))
    }

    @Test
    fun `parses negatives`() {
        assertEquals(Money(-320), Money.fromPesos("-3,20"))
    }

    @Test
    fun `round-trips with format`() {
        val original = Money(98765)
        assertEquals(original, Money.fromPesos(original.format()))
    }

    // ---- invalid input ----

    @Test
    fun `rejects non-numeric text`() {
        assertThrows(IllegalArgumentException::class.java) { Money.fromPesos("abc") }
    }

    @Test
    fun `rejects more than two decimals`() {
        assertThrows(IllegalArgumentException::class.java) { Money.fromPesos("12,345") }
    }

    @Test
    fun `rejects an empty string`() {
        assertThrows(IllegalArgumentException::class.java) { Money.fromPesos("   ") }
    }

    @Test
    fun `fromPesosOrNull returns null instead of throwing`() {
        assertNull(Money.fromPesosOrNull("abc"))
        assertNull(Money.fromPesosOrNull(""))
        assertNull(Money.fromPesosOrNull("12,34,567"))
    }

    // ---- factories ----

    @Test
    fun `fromPesos with Long multiplies by one hundred`() {
        assertEquals(Money(1500), Money.fromPesos(15L))
    }

    @Test
    fun `fromCents is identity`() {
        assertEquals(Money(42), Money.fromCents(42))
    }

    // ---- arithmetic ----

    @Test
    fun `addition and subtraction`() {
        assertEquals(Money(1500), Money(1000) + Money(500))
        assertEquals(Money(500), Money(1000) - Money(500))
    }

    @Test
    fun `multiplies by a quantity`() {
        assertEquals(Money(750), Money(250) * 3)
        assertEquals(Money(750), Money(250) * 3L)
    }

    @Test
    fun `unary negation`() {
        assertEquals(Money(-1000), -Money(1000))
    }

    @Test
    fun `margin as price minus cost`() {
        val cost = Money.fromPesos("4,00")
        val price = Money.fromPesos("10,00")
        assertEquals(Money(600), price - cost)
    }

    // ---- comparison and predicates ----

    @Test
    fun `is comparable`() {
        assertTrue(Money(500) < Money(1000))
        assertTrue(Money(1000) > Money(500))
        assertEquals(0, Money(700).compareTo(Money(700)))
    }

    @Test
    fun `sign predicates`() {
        assertTrue(Money.ZERO.isZero)
        assertTrue(Money(1).isPositive)
        assertTrue(Money(-1).isNegative)
        assertFalse(Money(-1).isPositive)
    }
}
