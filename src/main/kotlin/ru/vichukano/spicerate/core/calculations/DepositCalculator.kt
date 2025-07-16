package ru.vichukano.spicerate.core.calculations

import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.DepositDetails

interface DepositCalculator {

    fun calculateProfit(deposit: Deposit): DepositDetails

}