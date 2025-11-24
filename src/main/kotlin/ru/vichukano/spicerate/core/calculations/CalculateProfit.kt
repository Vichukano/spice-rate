package ru.vichukano.spicerate.core.calculations

import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.calculations.deposit.DepositCalculatorProvider
import ru.vichukano.spicerate.core.model.DepositRequest
import ru.vichukano.spicerate.core.model.DepositDetails

class CalculateProfit() : (DepositRequest) -> DepositDetails {

    override fun invoke(depositRequest: DepositRequest): DepositDetails {
        log.debug("Start to calculate deposit: {}", depositRequest)
        val depositDetails = DepositCalculatorProvider.calculateProfit(depositRequest)
        log.debug("Calculated deposit details: {}", depositDetails)
        return depositDetails
    }

    private companion object {
        private val log = LoggerFactory.getLogger(CalculateProfit::class.java)
    }

}
