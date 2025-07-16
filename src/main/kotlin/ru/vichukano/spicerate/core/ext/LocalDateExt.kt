package ru.vichukano.spicerate.core.ext

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean = this.isBefore(other) || this.isEqual(other)

fun LocalDate.shiftToEndOfQuarter(): LocalDate {
    val quarter = ((this.monthValue - 1) / 3) * 3 + 1
    return this.withMonth(quarter).with(TemporalAdjusters.lastDayOfMonth())
}
