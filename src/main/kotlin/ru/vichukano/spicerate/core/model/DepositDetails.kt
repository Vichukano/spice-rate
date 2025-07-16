package ru.vichukano.spicerate.core.model

import java.time.LocalDate
import java.util.*

data class DepositDetails(
    val depositId: UUID,
    val startSum: Amount,
    val endSum: Amount,
    val delta: Amount,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val baseRate: Rate,
    val effectiveRate: Rate,
    val capitalization: Capitalization,
    val statistics: Map<LocalDate, Amount>
)