package ru.vichukano.spicerate.core.model

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class Deposit(
    val sum: Amount,
    val openDate: LocalDate,
    val periodMonths: Int,
    val rate: Rate,
    val capitalization: Capitalization,
    val id: UUID = UUID.randomUUID(),
    val createdAt: Instant = Instant.now(),
)
