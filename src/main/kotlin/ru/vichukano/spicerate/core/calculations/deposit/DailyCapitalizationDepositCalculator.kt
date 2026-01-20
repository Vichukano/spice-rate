package ru.vichukano.spicerate.core.calculations.deposit

import ru.vichukano.spicerate.core.ext.isBeforeOrEqual
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.core.model.DepositRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

internal object DailyCapitalizationDepositCalculator : DepositCalculator {

    override fun calculateProfit(depositRequest: DepositRequest): DepositDetails {
        val startDate = depositRequest.openDate
        val endDate = depositRequest.endDate ?: startDate.plusMonths(depositRequest.termInMonths.toLong())
        val startSum = depositRequest.sum.minimalUnits()
        val daysToDelta = HashMap<LocalDate, BigDecimal>()
        var curDate = startDate.plusDays(1)
        val rateValue = depositRequest.rate.decimalValue()
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
        val ear = EarCalculator.ear(depositRequest.rate, depositRequest.capitalization, depositRequest.termInMonths)
        return DepositDetails(
            startSum = Amount.create(startSum),
            endSum = Amount.create(endSum),
            profit = Amount.create(delta),
            startDate = startDate,
            endDate = endDate,
            baseRate = depositRequest.rate,
            effectiveRate = ear,
            capitalization = depositRequest.capitalization,
            statistics = statistics,
            dailyStatistics = statistics.toMap(),
            termInMonths = depositRequest.termInMonths,
            description = depositRequest.description
        )
    }

}