package ru.vichukano.spicerate.core.calculations

import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.calculations.deposit.DepositCalculatorProvider
import ru.vichukano.spicerate.core.calculations.replenishment.DepositDetailsMerger.merge
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.core.model.DepositRequest
import ru.vichukano.spicerate.core.model.ReplenishmentCommand
import ru.vichukano.spicerate.core.storage.ExposedCalculationRepository
import java.time.LocalDate
import java.time.Period

class ReplenishDeposit(
    private val repository: ExposedCalculationRepository,
) : (ReplenishmentCommand) -> DepositDetails {

    override fun invoke(replenishment: ReplenishmentCommand): DepositDetails {
        log.debug("Start to apply replenishment: {}", replenishment)
        val depositDetails: DepositDetails = requireNotNull(repository.findById(replenishment.depositId))
        if (replenishment.replenishmentDate.isBefore(depositDetails.startDate)) {
            throw IllegalStateException(
                "Replenishment date: ${replenishment.replenishmentDate} " +
                        "is before deposit open date: ${depositDetails.startDate}"
            )
        }
        val replenishmentPart = DepositRequest(
            sum = replenishment.sum,
            openDate = replenishment.replenishmentDate,
            termInMonths = calculateTermInMonths(
                depositDetails.startDate,
                replenishment.replenishmentDate,
                depositDetails.termInMonths
            ),
            rate = depositDetails.baseRate,
            capitalization = depositDetails.capitalization,
            endDate = depositDetails.endDate,
            description = ""
        )
        val replenishmentDetails: DepositDetails = DepositCalculatorProvider.calculateProfit(replenishmentPart)
        val updatedDetails: DepositDetails = merge(depositDetails, replenishmentDetails, replenishment).also {
            log.debug("Deposit details after replenishment: {}", it)
        }
        repository.update(updatedDetails)
        return updatedDetails
    }

    private fun calculateTermInMonths(
        openDate: LocalDate,
        replenishmentDate: LocalDate,
        originalTerm: Int,
    ): Int {
        val period = Period.between(openDate, replenishmentDate)
        return (originalTerm - period.months).also {
            log.debug("Calculated term for replenishment: $it")
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(ReplenishDeposit::class.java)
    }

}