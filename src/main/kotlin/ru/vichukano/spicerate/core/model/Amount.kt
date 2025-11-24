package ru.vichukano.spicerate.core.model

import java.math.BigInteger

class Amount private constructor(
    private val minimalUnits: BigInteger,
    private val intPart: Long,
    private val decimalPart: String,
) {

    operator fun plus(other: Amount) = create(this.minimalUnits + other.minimalUnits)

    companion object {
        private val HUNDRED = BigInteger("100")

        fun create(integerPart: Long, decimalPart: Int): Amount {
            if (decimalPart > 99) {
                throw IllegalArgumentException("Decimal part should be between 0 and 99, input value: $decimalPart")
            }
            if (integerPart < 0 || decimalPart < 0) {
                throw IllegalArgumentException("Integer and decimal part of amount should be positive!")
            }
            val formattedDecimalPart = formatDecimalPart(decimalPart)
            val minimalUnits = BigInteger("$integerPart$formattedDecimalPart")
            return Amount(minimalUnits, integerPart, formattedDecimalPart)
        }

        fun create(minimalUnits: BigInteger): Amount {
            if (minimalUnits < BigInteger.ZERO) {
                throw IllegalArgumentException("Amount must be greater than zero!")
            }
            val intPart = minimalUnits.divide(HUNDRED).toLong()
            val decimalPart = minimalUnits.remainder(HUNDRED).toInt()
            return Amount(minimalUnits, intPart, formatDecimalPart(decimalPart))
        }

        fun create(minimalUnits: Long): Amount {
            if (minimalUnits < 0L) {
                throw IllegalArgumentException("Amount must be greater than zero!")
            }
            return create(BigInteger.valueOf(minimalUnits))
        }

        fun create(stringValue: String): Amount = try {
            create(BigInteger(stringValue))
        } catch (e: Exception) {
            throw IllegalArgumentException("Can't create Amount from string value: $stringValue}", e)
        }

        private fun formatDecimalPart(decimalPart: Int): String = when (decimalPart) {
            0 -> "00"
            1 -> "01"
            2 -> "02"
            3 -> "03"
            4 -> "04"
            5 -> "05"
            6 -> "06"
            7 -> "07"
            8 -> "08"
            9 -> "09"
            else -> "$decimalPart"
        }
    }

    fun minimalUnits(): BigInteger = minimalUnits

    override fun toString(): String = "$intPart,$decimalPart"

    fun toFormattedString(): String {
        val integerPartString = intPart.toString()
        val formattedIntegerPart = integerPartString.reversed()
            .chunked(3)
            .joinToString("_")
            .reversed()
        return "$formattedIntegerPart,$decimalPart"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Amount
        if (intPart != other.intPart) return false
        if (decimalPart != other.decimalPart) return false
        if (minimalUnits != other.minimalUnits) return false
        return true
    }

    override fun hashCode(): Int {
        var result = intPart
        result = 31 * result + decimalPart.hashCode()
        result = 31 * result + minimalUnits.hashCode()
        return result.toInt()
    }

}