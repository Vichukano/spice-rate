package ru.vichukano.spicerate.core.model

import java.math.BigDecimal
import java.math.RoundingMode

class Rate private constructor(private val value: BigDecimal) {

    companion object {
        private val HUNDRED = BigDecimal("100")

        fun create(stringValue: String): Rate {
            if (stringValue.length > 5) {
                throw IllegalArgumentException("Rate value must contain 5 chars or less, current chars value: ${stringValue.length}")
            }
            val formattedValue = BigDecimal(stringValue.replace(',', '.'))
                .divide(HUNDRED)
                .setScale(4, RoundingMode.HALF_UP)
            return Rate(formattedValue)
        }
    }

    fun decimalValue() = value

    override fun toString(): String {
        val formatted = value.multiply(HUNDRED)
            .setScale(2, RoundingMode.HALF_UP)
        return "$formatted%".replace('.', ',')
    }

}