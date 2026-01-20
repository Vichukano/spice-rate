package ru.vichukano.spicerate.gui.controller

import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.calculations.CalculateProfit
import ru.vichukano.spicerate.core.calculations.ReplenishDeposit
import ru.vichukano.spicerate.core.model.*
import ru.vichukano.spicerate.core.storage.CalculationRepository
import ru.vichukano.spicerate.core.model.ReplenishmentCommand
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class DepositCalculationsController(
    private val calculateProfit: CalculateProfit,
    private val repository: CalculationRepository,
    private val replenishDeposit: ReplenishDeposit
) {
    private var lastCalculatedDepositRequest: DepositRequest? = null
    private var lastCalculatedDetails: DepositDetails? = null

    fun calculateDeposit(
        inputSum: String,
        inputRate: String,
        periodMonths: Int,
        capitalizationValue: String,
        openDate: LocalDate,
        description: String
    ): DepositDetails {
        val amountRaw = BigDecimal(inputSum.replace("_", "").replace(',', '.'))
            .multiply(BigDecimal(100))
            .setScale(2)
            .toBigInteger()
        val depositRequest = DepositRequest(
            sum = Amount.create(amountRaw),
            rate = Rate.create(inputRate),
            termInMonths = periodMonths,
            capitalization = Capitalization.fromValue(capitalizationValue),
            description = description.take(256),
            openDate = openDate,
        )
        log.debug("Calculating for deposit: {}", depositRequest)
        val details = calculateProfit(depositRequest)
        lastCalculatedDepositRequest = depositRequest
        lastCalculatedDetails = details
        return details
    }

    fun saveLastCalculation() {
        val deposit = lastCalculatedDepositRequest
        val details = lastCalculatedDetails
        if (deposit != null && details != null) {
            repository.save(details)
            log.debug("Saved calculation for deposit: {}", deposit)
        } else {
            log.warn("No calculation to save")
        }
    }

    fun getAllCalculations(): List<DepositDetails> {
        return repository.findAll()
    }

    fun delete(id: String) {
        log.debug("Deleting calculation with id: {}", id)
        repository.deleteById(UUID.fromString(id))
    }

    fun getDetailsById(id: String): DepositDetails? {
        log.debug("Getting details for calculation with id: {}", id)
        return repository.findById(UUID.fromString(id))
            .also { log.debug("Found details: {}", it) }
    }

    fun updateDepositDetails(details: DepositDetails) {
        repository.update(details)
        log.debug("Updated calculation for deposit: {}", details.id)
    }

    fun replenish(replenishment: ReplenishmentCommand) {
        log.info("Replenishing deposit with replenishment: {}", replenishment)
        runCatching { replenishDeposit(replenishment) }
            .onFailure { log.error("Failed to replenish with: {}", replenishment, it) }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(DepositCalculationsController::class.java)
    }

}