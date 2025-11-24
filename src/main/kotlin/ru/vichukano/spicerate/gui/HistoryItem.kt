package ru.vichukano.spicerate.gui

import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Rate

data class HistoryItem(
    val id: String,
    val openDate: String,
    val rate: String,
    val effectiveRate: Rate,
    val capitalization: String,
    val amount: Amount,
    val finalAmount: Amount
)
