package ru.vichukano.spicerate.core.model

import java.time.LocalDate

data class Replenishment(
    val sum: Amount,
    val date: LocalDate
)
