package com.cajaclara.app.core.money

import kotlin.math.abs

/**
 * A monetary amount in **Cuban pesos (CUP)**, stored internally as **cents** (`Long`)
 * to avoid floating-point errors.
 *
 * - Never use `Float`/`Double` to hold money.
 * - The user types pesos (e.g. "12,50"); the app converts to cents.
 * - Rendered in es-ES format: `12,50 CUP` (comma decimal, dot thousands separator).
 */
@JvmInline
value class Money(val cents: Long) : Comparable<Money> {

    val isZero: Boolean get() = cents == 0L
    val isNegative: Boolean get() = cents < 0L
    val isPositive: Boolean get() = cents > 0L

    operator fun plus(other: Money): Money = Money(cents + other.cents)
    operator fun minus(other: Money): Money = Money(cents - other.cents)
    operator fun times(factor: Int): Money = Money(cents * factor)
    operator fun times(factor: Long): Money = Money(cents * factor)
    operator fun unaryMinus(): Money = Money(-cents)

    override fun compareTo(other: Money): Int = cents.compareTo(other.cents)

    /** es-ES format, e.g. `1.234,56 CUP`. Negatives: `-12,50 CUP`. */
    fun format(): String {
        val negative = cents < 0L
        val absCents = abs(cents)
        val pesos = absCents / 100
        val centsPart = absCents % 100
        val sign = if (negative) "-" else ""
        return "$sign${groupThousands(pesos)},${centsPart.toString().padStart(2, '0')} $CURRENCY_CODE"
    }

    override fun toString(): String = format()

    companion object {
        /** ISO code of the app's currency. */
        const val CURRENCY_CODE = "CUP"

        val ZERO = Money(0)

        fun fromCents(cents: Long): Money = Money(cents)

        fun fromPesos(pesos: Long): Money = Money(pesos * 100)

        /**
         * Parses a peso amount entered by the user.
         *
         * Accepts comma or dot as the decimal separator, tolerates mixed thousands
         * separators, and ignores a trailing `CUP` code or `$` symbol:
         * `"12,50"`, `"12.50"`, `"12"`, `"1.234,56"`, `"1,234.56"`, `" 12,5 CUP "`, `"-3,20"`.
         * The last separator is treated as the decimal point; the rest as thousands.
         *
         * @throws IllegalArgumentException if the text is not a valid amount
         *         (empty, non-numeric characters, or more than 2 decimals).
         */
        fun fromPesos(input: String): Money =
            fromPesosOrNull(input)
                ?: throw IllegalArgumentException("Invalid peso amount: \"$input\"")

        /** Safe variant of [fromPesos]: returns `null` instead of throwing. */
        fun fromPesosOrNull(input: String): Money? {
            // Strip the currency code/symbol if present (uppercasing is safe: digits only).
            var s = input.uppercase().replace(CURRENCY_CODE, "").replace("$", "").replace("€", "").trim()
            if (s.isEmpty()) return null

            val negative = s.startsWith("-")
            if (negative) s = s.substring(1).trim()
            if (s.isEmpty()) return null

            val lastSep = maxOf(s.lastIndexOf(','), s.lastIndexOf('.'))
            val integerRaw: String
            val fractionRaw: String
            if (lastSep == -1) {
                integerRaw = s
                fractionRaw = ""
            } else {
                integerRaw = s.substring(0, lastSep)
                fractionRaw = s.substring(lastSep + 1)
            }

            // Drop thousands separators from the integer side.
            val integerDigits = integerRaw.replace(",", "").replace(".", "")
            // The integer side may be empty ("," -> 0), but then there must be decimals.
            if (integerDigits.isEmpty() && fractionRaw.isEmpty()) return null
            if (!integerDigits.all { it.isDigit() }) return null
            if (!fractionRaw.all { it.isDigit() }) return null
            if (fractionRaw.length > 2) return null

            val pesos = if (integerDigits.isEmpty()) 0L else integerDigits.toLong()
            val centsPart = fractionRaw.padEnd(2, '0').ifEmpty { "00" }.toLong()
            val total = pesos * 100 + centsPart
            return Money(if (negative) -total else total)
        }

        private fun groupThousands(value: Long): String {
            val digits = value.toString()
            if (digits.length <= 3) return digits
            val sb = StringBuilder()
            val firstGroup = digits.length % 3
            if (firstGroup > 0) {
                sb.append(digits, 0, firstGroup)
                sb.append('.')
            }
            var i = firstGroup
            while (i < digits.length) {
                sb.append(digits, i, i + 3)
                if (i + 3 < digits.length) sb.append('.')
                i += 3
            }
            return sb.toString()
        }
    }
}
