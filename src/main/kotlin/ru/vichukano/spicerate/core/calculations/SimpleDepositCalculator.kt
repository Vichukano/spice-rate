package ru.vichukano.spicerate.core.calculations

import ru.vichukano.spicerate.core.ext.isBeforeOrEqual
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.DepositDetails
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class SimpleDepositCalculator : DepositCalculator {

    override fun calculateProfit(deposit: Deposit): DepositDetails {
        val startDate = deposit.openDate
        val endDate = startDate.plusMonths(deposit.periodMonths.toLong())
        val startSum = deposit.sum.minimalUnits()
        val daysToDelta = HashMap<LocalDate, BigDecimal>()
        var curDate = startDate.plusDays(1)
        val rateValue = deposit.rate.decimalValue()
        while (curDate.isBeforeOrEqual(endDate)) {
            val daysInYear = if (curDate.isLeapYear) LEAP_YEAR_DAYS else NON_LEAP_YEAR_DAYS
            val delta = startSum.toBigDecimal()
                .multiply(rateValue)
                .divide(daysInYear, 10, RoundingMode.HALF_UP)
            daysToDelta[curDate] = delta
            curDate = curDate.plusDays(1)
        }
        val delta = daysToDelta.values
            .fold(BigDecimal.ZERO) { acc, next -> acc.add(next) }
            .setScale(2, RoundingMode.HALF_UP)
            .toBigInteger()
        val endSum = startSum.plus(delta)
        return DepositDetails(
            depositId = deposit.id,
            startSum = Amount.create(startSum),
            endSum = Amount.create(endSum),
            delta = Amount.create(delta),
            startDate = startDate,
            endDate = endDate,
            baseRate = deposit.rate,
            effectiveRate = deposit.rate,
            capitalization = deposit.capitalization,
            statistics = mapOf(endDate to Amount.create(delta))
        )
    }

    private companion object {
        private val LEAP_YEAR_DAYS = BigDecimal("366.0")
        private val NON_LEAP_YEAR_DAYS = BigDecimal("365.0")
    }
}