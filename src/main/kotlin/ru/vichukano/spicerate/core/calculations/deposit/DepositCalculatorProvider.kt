package ru.vichukano.spicerate.core.calculations.deposit

import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.DepositRequest
import ru.vichukano.spicerate.core.model.DepositDetails

object DepositCalculatorProvider : DepositCalculator {

    override fun calculateProfit(depositRequest: DepositRequest): DepositDetails {
        return when (depositRequest.capitalization) {
            Capitalization.NONE -> SimpleDepositCalculator.calculateProfit(depositRequest)
            Capitalization.DAY -> DailyCapitalizationDepositCalculator.calculateProfit(depositRequest)
            Capitalization.MONTH -> MonthlyCapitalizationDepositCalculator.calculateProfit(depositRequest)
            Capitalization.YEAR -> YearlyCapitalizationDepositCalculator.calculateProfit(depositRequest)
        }
    }

}
