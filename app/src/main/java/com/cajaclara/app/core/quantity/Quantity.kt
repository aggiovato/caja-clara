package com.cajaclara.app.core.quantity

/**
 * A non-negative count of units (stock on hand, sale line quantities).
 *
 * Wraps an `Int` with the invariant that a quantity is never negative. Arithmetic
 * that would drop below zero throws: removing more stock than exists is a domain
 * error the caller must prevent (validate before subtracting).
 */
@JvmInline
value class Quantity(val value: Int) : Comparable<Quantity> {
    init {
        require(value >= 0) { "Quantity cannot be negative: $value" }
    }

    val isZero: Boolean get() = value == 0
    val isPositive: Boolean get() = value > 0

    operator fun plus(other: Quantity): Quantity = Quantity(value + other.value)
    operator fun minus(other: Quantity): Quantity = Quantity(value - other.value)

    override fun compareTo(other: Quantity): Int = value.compareTo(other.value)

    companion object {
        val ZERO = Quantity(0)
    }
}
