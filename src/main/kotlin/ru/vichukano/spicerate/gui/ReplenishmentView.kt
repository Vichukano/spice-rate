package ru.vichukano.spicerate.gui

import javafx.application.Platform
import javafx.concurrent.Task
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.ReplenishmentCommand
import ru.vichukano.spicerate.gui.controller.DepositCalculationsController
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.thread

class ReplenishmentView(
    private val depositId: UUID,
    private val controller: DepositCalculationsController,
    private val onReplenished: () -> Unit
) : VBox() {

    private val amountField = DoubleValueTextField()
    private val datePicker = DatePicker(LocalDate.now())
    private val replenishButton = Button("Пополнить")

    init {
        padding = Insets(10.0)
        spacing = 10.0

        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0

        grid.add(Label("Сумма пополнения"), 0, 0)
        grid.add(amountField, 1, 0)
        grid.add(Label("Дата пополнения"), 0, 1)
        grid.add(datePicker, 1, 1)
        grid.add(replenishButton, 1, 2)

        children.add(grid)

        replenishButton.setOnAction {
            val amountValue = BigDecimal(amountField.text.replace(',', '.'))
            val replenishment = ReplenishmentCommand(
                depositId = depositId,
                sum = Amount.create(
                    amountValue.multiply(BigDecimal(100)).toBigInteger()
                ),
                replenishmentDate = datePicker.value
            )

            val task = object : Task<Unit>() {
                override fun call() {
                    controller.replenish(replenishment)
                }

                override fun succeeded() {
                    Platform.runLater {
                        onReplenished()
                        (scene.window as Stage).close()
                    }
                }
            }
            thread(isDaemon = true, block = task::run)
        }
    }
}
