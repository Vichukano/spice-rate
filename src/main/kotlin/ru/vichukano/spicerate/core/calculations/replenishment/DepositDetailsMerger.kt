package ru.vichukano.spicerate.core.calculations.replenishment

import ru.vichukano.spicerate.core.model.DepositDetails

internal object DepositDetailsMerger {

    fun merge(
        first: DepositDetails,
        second: DepositDetails,
    ): DepositDetails {
        val originalStats = first.statistics
        val updatedStats = originalStats.toMutableMap()
        for (entry in second.statistics) {
            originalStats[entry.key]?.let { amount ->
                updatedStats[entry.key] = amount + entry.value
            }
        }
        val originalDailyStats = first.dailyStatistics
        val updatedDaily = originalDailyStats.toMutableMap()
        for (entry in second.dailyStatistics) {
            originalDailyStats[entry.key]?.let { amount ->
                updatedDaily[entry.key] = amount + entry.value
            }
        }
        return DepositDetails(
            id = first.id,
            startSum = first.startSum,
            endSum = first.endSum + second.endSum,
            profit = first.profit + second.profit,
            startDate = first.startDate,
            endDate = first.endDate,
            baseRate = first.baseRate,
            effectiveRate = first.effectiveRate,
            capitalization = first.capitalization,
            statistics = updatedStats,
            dailyStatistics = updatedDaily,
            termInMonths = first.termInMonths,
        )
    }

}
