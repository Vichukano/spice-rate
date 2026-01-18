package ru.vichukano.spicerate.core.model

import java.time.LocalDate
import java.util.UUID

data class ReplenishmentCommand(
    val sum: Amount,
    val replenishmentDate: LocalDate,
    val depositId: UUID,
)
