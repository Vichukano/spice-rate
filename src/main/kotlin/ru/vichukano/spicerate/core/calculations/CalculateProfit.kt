package ru.vichukano.spicerate.core.calculations

import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.DepositDetails

class CalculateProfit(
    private val simple: DepositCalculator,
    private val daily: DepositCalculator,
    private val monthly: DepositCalculator,
    private val yearly: DepositCalculator,
) : (Deposit) -> DepositDetails {

    override fun invoke(deposit: Deposit): DepositDetails {
        log.debug("Start to calculate deposit: {}", deposit)
        val depositDetails = when (deposit.capitalization) {
            Capitalization.NONE -> simple.calculateProfit(deposit)
            Capitalization.DAY -> daily.calculateProfit(deposit)
            Capitalization.MONTH -> monthly.calculateProfit(deposit)
            Capitalization.YEAR -> yearly.calculateProfit(deposit)
        }
        log.debug("Calculated deposit details: {}", depositDetails)
        return depositDetails
    }

    private companion object {
        private val log = LoggerFactory.getLogger(CalculateProfit::class.java)
    }

}
