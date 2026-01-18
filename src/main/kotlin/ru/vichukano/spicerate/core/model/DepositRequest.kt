package ru.vichukano.spicerate.core.model

import java.time.Instant
import java.time.LocalDate

data class DepositRequest(
    val sum: Amount,
    val openDate: LocalDate,
    val termInMonths: Int,
    val rate: Rate,
    val capitalization: Capitalization,
    val createdAt: Instant = Instant.now(),
    var endDate: LocalDate? = null
)
