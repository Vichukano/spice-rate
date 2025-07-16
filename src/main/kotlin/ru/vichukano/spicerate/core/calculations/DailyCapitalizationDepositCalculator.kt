package ru.vichukano.spicerate.core.calculations

import ru.vichukano.spicerate.core.ext.isBeforeOrEqual
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.DepositDetails
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

class DailyCapitalizationDepositCalculator : DepositCalculator {

    override fun calculateProfit(deposit: Deposit): DepositDetails {
        val startDate = deposit.openDate
        val endDate = startDate.plusMonths(deposit.periodMonths.toLong())
        val startSum = deposit.sum.minimalUnits()
        val daysToDelta = HashMap<LocalDate, BigDecimal>()
        var curDate = startDate.plusDays(1)
        val rateValue = deposit.rate.decimalValue()
        var total = startSum.toBigDecimal()
        while (curDate.isBeforeOrEqual(endDate)) {
            val daysInYear = if (curDate.isLeapYear) LEAP_YEAR_DAYS else NON_LEAP_YEAR_DAYS
            val part = rateValue.divide(daysInYear, 10, RoundingMode.HALF_UP)
            val delta = total.multiply(part)
            total = total.add(delta)
            daysToDelta[curDate] = total
            curDate = curDate.plusDays(1)
        }
        val endSum = total.setScale(2, RoundingMode.HALF_UP).toBigInteger()
        val delta = endSum.subtract(startSum)
        val statistics: SortedMap<LocalDate, Amount> = daysToDelta
            .mapValues {
                Amount.create(
                    it.value.setScale(2, RoundingMode.HALF_UP).toBigInteger()
                )
            }
            .toSortedMap()
        val ear = EarCalculator.ear(deposit.rate, deposit.capitalization, deposit.periodMonths)
        return DepositDetails(
            depositId = deposit.id,
            startSum = Amount.create(startSum),
            endSum = Amount.create(endSum),
            delta = Amount.create(delta),
            startDate = startDate,
            endDate = endDate,
            baseRate = deposit.rate,
            effectiveRate = ear,
            capitalization = deposit.capitalization,
            statistics = statistics
        )
    }

    private companion object {
        private val LEAP_YEAR_DAYS = BigDecimal("366.0")
        private val NON_LEAP_YEAR_DAYS = BigDecimal("365.0")
    }

}