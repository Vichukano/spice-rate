package ru.vichukano.spicerate.core.storage

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object CalculationTable : Table("calculations") {
    val id = uuid("id")
    val startSum = long("start_sum")
    val baseRate = decimal("base_rate", 7, 4)
    val termInMonths = integer("term_in_months")
    val startDate = date("start_date")
    val endDate = date("end_date")
    val capitalization = varchar("capitalization", 50)
    val description = varchar("description", 256)
    val endSum = long("end_sum")
    val profit = long("profit")
    val effectiveRate = decimal("effective_rate", 7, 4)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
