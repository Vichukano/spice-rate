package ru.vichukano.spicerate.gui

data class TableItem(
    val number: String,
    val date: String,
    val amount: String,
)

data class ReplenishmentTableItem(
    val date: String,
    val amount: String
)
