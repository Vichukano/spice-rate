package ru.vichukano.spicerate.gui

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.*
import javafx.scene.text.Text
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.gui.controller.DepositCalculationsController
import java.time.LocalDate

class MainView(
    private val calculationsController: DepositCalculationsController
) : AnchorPane() {
    private val inputSum = DoubleValueTextField()
    private val inputRate = DoubleValueTextField()
    private val inputOpenDate = DatePicker()
    private val inputPeriodMonths = IntegerValueTextField()
    private val inputCapitalization = ComboBox<String>()
    private val calculateButton = Button("Рассчитать")
    private val saveButton = Button("Сохранить")
    private val historyButton = Button("История")
    private val outputInitialSum = TextField()
    private val outputSum = TextField()
    private val outputDelta = TextField()
    private val outputEar = TextField()
    private val outputStatisticsPane = TableView<TableItem>()
    private var historyView: HistoryView? = null

    init {
        prefHeight = 800.0
        prefWidth = 1200.0
        children.add(createMainLayout())
        initControls()
        initActions()
    }

    private fun createMainLayout(): SplitPane {
        val mainSplitPane = SplitPane().apply {
            orientation = javafx.geometry.Orientation.VERTICAL
            setDividerPositions(0.5)
            setPrefSize(800.0, 600.0)
            setTopAnchor(this, 0.0)
            setBottomAnchor(this, 0.0)
            setLeftAnchor(this, 0.0)
            setRightAnchor(this, 0.0)
        }
        val topSplitPane = SplitPane().apply {
            setDividerPositions(0.5)
            items.addAll(createInputGrid(), createOutputGrid())
        }
        initStatisticsPane()
        mainSplitPane.items.addAll(topSplitPane, outputStatisticsPane)
        return mainSplitPane
    }

    private fun createInputGrid(): GridPane {
        return GridPane().apply {
            prefHeight = 244.0
            prefWidth = 295.0
            columnConstraints.addAll(
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 },
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 }
            )
            repeat(6) {
                rowConstraints.add(RowConstraints().apply {
                    minHeight = 10.0; prefHeight = 30.0; vgrow = Priority.SOMETIMES
                })
            }
            fun addRow(rowIndex: Int, labelText: String, control: Node) {
                val text = Text(labelText)
                add(text, 0, rowIndex)
                add(control, 1, rowIndex)
                GridPane.setMargin(text, Insets(0.0, 0.0, 0.0, 20.0))
                GridPane.setMargin(control, Insets(0.0, 10.0, 0.0, 10.0))
            }
            addRow(0, "Сумма", inputSum)
            addRow(1, "Процентная ставка", inputRate)
            addRow(2, "Дата открытия", inputOpenDate)
            addRow(3, "Срок в месяцах", inputPeriodMonths)
            addRow(4, "Капитализация", inputCapitalization)
            add(calculateButton, 0, 5)
            GridPane.setMargin(calculateButton, Insets(0.0, 20.0, 0.0, 20.0))
            calculateButton.prefHeight = 30.0
            calculateButton.prefWidth = 185.0
        }
    }

    private fun createOutputGrid(): GridPane {
        return GridPane().apply {
            prefHeight = 294.0
            prefWidth = 395.0
            columnConstraints.addAll(
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 },
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 }
            )
            repeat(5) {
                rowConstraints.add(RowConstraints().apply {
                    minHeight = 10.0; prefHeight = 30.0; vgrow = Priority.SOMETIMES
                })
            }
            fun addRow(rowIndex: Int, labelText: String, node: Node) {
                val text = Text(labelText)
                add(text, 0, rowIndex)
                add(node, 1, rowIndex)
                GridPane.setMargin(text, Insets(0.0, 0.0, 0.0, 20.0))
                GridPane.setMargin(node, Insets(0.0, 10.0, 0.0, 20.0))
            }
            addRow(0, "Сумма в начале срока", outputInitialSum.apply { isEditable = false })
            addRow(1, "Сумма в конце срока", outputSum.apply { isEditable = false })
            addRow(2, "Доход", outputDelta.apply { isEditable = false })
            addRow(3, "Эффективная ставка", outputEar.apply { isEditable = false })
            val buttons = HBox().apply {
                spacing = 10.0
                children.addAll(saveButton, historyButton)
            }
            add(buttons, 0, 4)
            GridPane.setMargin(buttons, Insets(0.0, 20.0, 0.0, 20.0))
            saveButton.prefHeight = 30.0
            saveButton.prefWidth = 185.0
            historyButton.prefHeight = 30.0
            historyButton.prefWidth = 185.0
        }
    }

    private fun initControls() {
        inputSum.text = "100500,00"
        inputRate.text = "10,00"
        inputPeriodMonths.text = "12"
        inputOpenDate.value = LocalDate.now()
        inputCapitalization.value = Capitalization.NONE.value
        inputCapitalization.items.addAll(Capitalization.entries.map { it.value })
    }

    private fun initActions() {
        calculateButton.setOnAction { onCalculateButtonPressed() }
        saveButton.setOnAction { onSaveButtonPressed() }
        historyButton.setOnAction { onHistoryButtonPressed() }
    }

    private fun onHistoryButtonPressed() {
        if (historyView?.isShowing == true) {
            historyView?.requestFocus()
        } else {
            val calculations = calculationsController.getAllCalculations()
            historyView = HistoryView(
                controller = calculationsController,
                calculations = calculations,
                onClose = { historyView = null },
                onDelete = { item ->
                    calculationsController.delete(item.id)
                    historyView?.update(calculationsController.getAllCalculations())
                }
            )
            historyView?.show()
        }
    }

    private fun onSaveButtonPressed() {
        calculationsController.saveLastCalculation()
        if (historyView?.isShowing == true) {
            historyView?.update(calculationsController.getAllCalculations())
        }
    }

    private fun onCalculateButtonPressed() {
        val depositInfo = calculationsController.calculateDeposit(
            inputSum = inputSum.text,
            inputRate = inputRate.text,
            periodMonths = inputPeriodMonths.text.toInt(),
            capitalizationValue = inputCapitalization.value,
            openDate = inputOpenDate.value,
        )
        updateOutputs(depositInfo)
    }

    private fun updateOutputs(depositInfo: DepositDetails) {
        outputInitialSum.text = depositInfo.startSum.toFormattedString()
        outputSum.text = depositInfo.endSum.toFormattedString()
        outputDelta.text = depositInfo.profit.toFormattedString()
        outputEar.text = depositInfo.effectiveRate.toString()
        populateStatisticsPane(depositInfo.statistics)
    }

    private fun populateStatisticsPane(statistics: Map<LocalDate, Amount>) {
        outputStatisticsPane.items.clear()
        var index = 1
        statistics.forEach { (date, amount) ->
            outputStatisticsPane.items.add(
                TableItem(
                    number = "$index",
                    date = date.toString(),
                    amount = amount.toFormattedString(),
                )
            )
            index++
        }
        outputStatisticsPane.isVisible = true
    }

    private fun initStatisticsPane() {
        outputStatisticsPane.isVisible = false
        outputStatisticsPane.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        outputStatisticsPane.columns.addAll(
            createTableColumn("Номер", "number"),
            createTableColumn("Дата", "date"),
            createTableColumn("Сумма", "amount")
        )
    }

    private fun createTableColumn(name: String, propertyName: String): TableColumn<TableItem, String> {
        return TableColumn<TableItem, String>(name).apply {
            cellValueFactory = PropertyValueFactory(propertyName)
            isSortable = false
        }
    }
}
