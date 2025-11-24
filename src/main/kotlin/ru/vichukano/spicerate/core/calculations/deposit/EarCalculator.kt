package ru.vichukano.spicerate.core.calculations.deposit

import ch.obermuhlner.math.big.BigDecimalMath
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.Rate
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

object EarCalculator {

    fun ear(
        baseRate: Rate,
        capitalization: Capitalization,
        months: Int,
    ): Rate {
        val capPeriodValue = when (capitalization) {
            Capitalization.NONE -> BigDecimal.ZERO
            Capitalization.DAY -> BigDecimal("365.0")
            Capitalization.MONTH -> BigDecimal("12.0")
            Capitalization.YEAR -> BigDecimal.ONE
        }
        if (capPeriodValue == BigDecimal.ZERO) {
            return baseRate
        }
        val periodInYears = BigInteger(months.toString()).toBigDecimal()
            .divide(BigDecimal("12.0"), 10, RoundingMode.HALF_UP)
        val a = baseRate.decimalValue()
            .divide(capPeriodValue, 10, RoundingMode.HALF_UP)
            .plus(BigDecimal.ONE)
        val b = capPeriodValue.multiply(periodInYears)
        val pow = BigDecimalMath.pow(a, b, MathContext.DECIMAL64)
        val ear = pow
            .minus(BigDecimal.ONE)
            .divide(periodInYears, 10, RoundingMode.HALF_UP)
            .multiply(BigDecimal("100.00"))
            .setScale(2, RoundingMode.HALF_UP)
        return Rate.create(ear.toString())
    }

}