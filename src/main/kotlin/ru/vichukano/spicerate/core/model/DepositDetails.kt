package ru.vichukano.spicerate.core.model

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class DepositDetails(
    val id: UUID = UUID.randomUUID(),
    val createdAt: Instant = Instant.now(),
    val startSum: Amount,
    val endSum: Amount,
    val profit: Amount,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val baseRate: Rate,
    val termInMonths: Int,
    val effectiveRate: Rate,
    val capitalization: Capitalization,
    val description: String,
    val dailyStatistics: Map<LocalDate, Amount> = mapOf(),
    val statistics: Map<LocalDate, Amount>,
    val replenishments: List<Replenishment> = emptyList()
)