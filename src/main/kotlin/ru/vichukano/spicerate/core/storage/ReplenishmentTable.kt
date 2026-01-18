package ru.vichukano.spicerate.core.storage

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object ReplenishmentTable : Table("replenishments") {
    val id = long("id").autoIncrement()
    val calculationId = uuid("calculation_id").references(CalculationTable.id)
    val date = date("date")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(id)
}
