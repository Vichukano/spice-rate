package ru.vichukano.spicerate.gui

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.stage.Stage
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.gui.controller.DepositCalculationsController
import java.time.LocalDate

class DetailsView(
    private val depositDetails: DepositDetails,
    private val controller: DepositCalculationsController
) : VBox() {
    private val outputInitialSum = TextField()
    private val outputSum = TextField()
    private val outputDelta = TextField()
    private val outputBaseRate = TextField()
    private val outputEar = TextField()
    private val outputCapitalization = TextField()
    private val descriptionField = TextArea()
    private val saveDescriptionButton = Button("Сохранить описание")
    private val outputStatisticsPane = TableView<TableItem>()
    private val replenishmentPane = TableView<ReplenishmentTableItem>()
    private var stage: Stage? = null

    init {
        prefHeight = 600.0
        prefWidth = 800.0
        spacing = 10.0
        padding = Insets(10.0)
        children.add(createMainLayout())
        updateOutputs(depositDetails)
        descriptionField.isWrapText = true
        descriptionField.prefRowCount = 3
        descriptionField.prefHeight = 80.0
        saveDescriptionButton.setOnAction { saveDescription() }
        children.addAll(descriptionField, saveDescriptionButton)
    }

    private fun saveDescription() {
        val updatedDetails = depositDetails.copy(description = descriptionField.text.take(256))
        controller.updateDepositDetails(updatedDetails)
        updateView(updatedDetails)
    }

    fun show(onClose: () -> Unit = {}) {
        stage = Stage()
        stage?.let {
            it.title = "Детальная информация"
            it.scene = Scene(this, 800.0, 600.0)
            it.setOnCloseRequest { onClose() }
            it.show()
        }
    }

    fun updateView(depositDetails: DepositDetails) {
        updateOutputs(depositDetails)
    }

    val isShowing: Boolean
        get() = stage?.isShowing == true

    override fun requestFocus() {
        stage?.requestFocus()
    }

    private fun createMainLayout(): SplitPane {
        val mainSplitPane = SplitPane().apply {
            orientation = javafx.geometry.Orientation.VERTICAL
            setDividerPositions(0.5)
            setPrefSize(800.0, 600.0)
        }
        initStatisticsPane()
        initReplenishmentPane()
        val statisticsVBox = VBox().apply {
            alignment = Pos.CENTER
            children.addAll(Label("Выплаты"), outputStatisticsPane)
            setVgrow(outputStatisticsPane, Priority.ALWAYS)
        }
        val replenishmentVBox = VBox().apply {
            alignment = Pos.CENTER
            children.addAll(Label("Пополнения"), replenishmentPane)
            setVgrow(replenishmentPane, Priority.ALWAYS)
        }
        val bottomSplitPane = SplitPane().apply {
            orientation = javafx.geometry.Orientation.HORIZONTAL
            setDividerPositions(0.5)
            items.addAll(statisticsVBox, replenishmentVBox)
        }
        mainSplitPane.items.addAll(createOutputGrid(), bottomSplitPane)
        return mainSplitPane
    }

    private fun createOutputGrid(): GridPane {
        return GridPane().apply {
            prefHeight = 294.0
            prefWidth = 395.0
            columnConstraints.addAll(
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 },
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 }
            )
            repeat(6) {
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
            addRow(3, "Базовая ставка", outputBaseRate.apply { isEditable = false })
            addRow(4, "Эффективная ставка", outputEar.apply { isEditable = false })
            addRow(5, "Вид капитализации", outputCapitalization.apply { isEditable = false })
        }
    }

    private fun updateOutputs(depositInfo: DepositDetails) {
        outputInitialSum.text = depositInfo.startSum.toFormattedString()
        outputSum.text = depositInfo.endSum.toFormattedString()
        outputDelta.text = depositInfo.profit.toFormattedString()
        outputBaseRate.text = depositInfo.baseRate.toString()
        outputEar.text = depositInfo.effectiveRate.toString()
        outputCapitalization.text = depositInfo.capitalization.value
        descriptionField.text = depositInfo.description
        populateStatisticsPane(depositInfo.statistics)
        populateReplenishmentPane(depositInfo.replenishments)
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

    private fun populateReplenishmentPane(replenishments: List<ru.vichukano.spicerate.core.model.Replenishment>) {
        replenishmentPane.items.clear()
        var index = 1
        replenishments.forEach {
            replenishmentPane.items.add(
                ReplenishmentTableItem(
                    number = "$index",
                    date = it.date.toString(),
                    amount = it.sum.toFormattedString()
                )
            )
            index++
        }
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

    private fun initReplenishmentPane() {
        replenishmentPane.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        replenishmentPane.columns.addAll(
            createReplenishmentTableColumn("Номер", "number"),
            createReplenishmentTableColumn("Дата", "date"),
            createReplenishmentTableColumn("Сумма", "amount")
        )
    }

    private fun createTableColumn(name: String, propertyName: String): TableColumn<TableItem, String> {
        return TableColumn<TableItem, String>(name).apply {
            cellValueFactory = PropertyValueFactory(propertyName)
            isSortable = false
        }
    }

    private fun createReplenishmentTableColumn(
        name: String,
        propertyName: String
    ): TableColumn<ReplenishmentTableItem, String> {
        return TableColumn<ReplenishmentTableItem, String>(name).apply {
            cellValueFactory = PropertyValueFactory(propertyName)
            isSortable = false
        }
    }
}
