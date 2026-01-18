package ru.vichukano.spicerate.core.calculations.deposit

import ru.vichukano.spicerate.core.ext.isBeforeOrEqual
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.core.model.DepositRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

internal object SimpleDepositCalculator : DepositCalculator {

    override fun calculateProfit(depositRequest: DepositRequest): DepositDetails {
        val startDate = depositRequest.openDate
        val endDate = depositRequest.endDate ?: startDate.plusMonths(depositRequest.termInMonths.toLong())
        val startSum = depositRequest.sum.minimalUnits()
        val daysToDelta = HashMap<LocalDate, BigDecimal>()
        var curDate = startDate.plusDays(1)
        val rateValue = depositRequest.rate.decimalValue()
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
            startSum = Amount.create(startSum),
            endSum = Amount.create(endSum),
            profit = Amount.create(delta),
            startDate = startDate,
            endDate = endDate,
            baseRate = depositRequest.rate,
            effectiveRate = depositRequest.rate,
            capitalization = depositRequest.capitalization,
            statistics = mapOf(endDate to Amount.create(delta)),
            dailyStatistics = daysToDelta.mapValues {
                Amount.create(
                    it.value.setScale(2, RoundingMode.HALF_UP).toBigInteger()
                )
            },
            termInMonths = depositRequest.termInMonths,
        )
    }

}