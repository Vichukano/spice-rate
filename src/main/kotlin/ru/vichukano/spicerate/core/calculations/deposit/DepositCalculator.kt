package ru.vichukano.spicerate.core.calculations.deposit

import ru.vichukano.spicerate.core.model.DepositRequest
import ru.vichukano.spicerate.core.model.DepositDetails

interface DepositCalculator {

    fun calculateProfit(depositRequest: DepositRequest): DepositDetails

}