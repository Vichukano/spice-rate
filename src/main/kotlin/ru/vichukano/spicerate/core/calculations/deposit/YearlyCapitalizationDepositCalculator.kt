package ru.vichukano.spicerate.core.calculations.deposit

import ru.vichukano.spicerate.core.ext.isBeforeOrEqual
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.core.model.DepositRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

internal object YearlyCapitalizationDepositCalculator : DepositCalculator {

    override fun calculateProfit(depositRequest: DepositRequest): DepositDetails {
        val startDate = depositRequest.openDate
        val endDate = startDate.plusMonths(depositRequest.termInMonths.toLong())
        val startSum = depositRequest.sum.minimalUnits()
        val yearsToDelta = HashMap<LocalDate, BigDecimal>()
        val buffer = ArrayList<BigDecimal>()
        var currDate = startDate.plusDays(1)
        var checkPointDate = currDate.plusYears(1)
        val rateValue = depositRequest.rate.decimalValue()
        var total = startSum.toBigDecimal()
        val dailyStatistic = mutableMapOf<LocalDate, Amount>()
        while (currDate.isBeforeOrEqual(endDate)) {
            val daysInYear = if (currDate.isLeapYear) LEAP_YEAR_DAYS else NON_LEAP_YEAR_DAYS
            if (currDate == checkPointDate) {
                val delta = buffer.fold(BigDecimal.ZERO) { acc, delta -> acc.add(delta) }
                total += delta
                buffer.clear()
                yearsToDelta[checkPointDate.minusDays(1)] = delta
                checkPointDate = checkPointDate.plusYears(1)
            }
            val delta = total.multiply(rateValue)
                .divide(daysInYear, 10, RoundingMode.HALF_UP)
            buffer.add(delta)
            currDate = currDate.plusDays(1)
            dailyStatistic[currDate] = Amount.create(delta.setScale(2, RoundingMode.HALF_UP).toBigInteger())
        }
        val lastDelta = buffer.fold(BigDecimal.ZERO) { acc, delta -> acc.add(delta) }
        yearsToDelta[checkPointDate.minusDays(1)] = lastDelta
        total += lastDelta
        val endSum = total.setScale(2, RoundingMode.HALF_UP).toBigInteger()
        val delta = endSum.subtract(startSum)
        val statistics: SortedMap<LocalDate, Amount> = yearsToDelta
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
            dailyStatistics = dailyStatistic,
            termInMonths = depositRequest.termInMonths,
        )
    }


}