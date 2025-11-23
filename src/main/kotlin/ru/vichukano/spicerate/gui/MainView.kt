package ru.vichukano.spicerate.gui

import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.*
import javafx.scene.text.Text
import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.calculations.CalculateProfit
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.core.model.Rate
import java.math.BigDecimal
import java.time.LocalDate

class MainView(
    private val calculateProfit: CalculateProfit
) : AnchorPane() {
    private val inputSum = DoubleValueTextField()
    private val inputRate = DoubleValueTextField()
    private val inputOpenDate = DatePicker()
    private val inputPeriodMonths = IntegerValueTextField()
    private val inputCapitalization = ComboBox<String>()
    private val calculateButton = Button("Рассчитать")
    private val outputInitialSum = Text()
    private val outputSum = Text()
    private val outputDelta = Text()
    private val outputEar = Text()
    private val outputStatisticsPane = TableView<TableItem>()

    init {
        prefHeight = 600.0
        prefWidth = 800.0
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
            fun addRow(rowIndex: Int, labelText: String, control: javafx.scene.Node) {
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
            layoutX = 6.0
            layoutY = 68.0
            prefHeight = 294.0
            prefWidth = 395.0
            columnConstraints.addAll(
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 },
                ColumnConstraints().apply { hgrow = Priority.SOMETIMES; minWidth = 10.0; prefWidth = 100.0 }
            )
            repeat(4) {
                rowConstraints.add(RowConstraints().apply {
                    minHeight = 10.0; prefHeight = 30.0; vgrow = Priority.SOMETIMES
                })
            }
            fun addRow(rowIndex: Int, labelText: String, node: javafx.scene.Node) {
                val text = Text(labelText)
                add(text, 0, rowIndex)
                add(node, 1, rowIndex)
                GridPane.setMargin(text, Insets(0.0, 0.0, 0.0, 20.0))
                GridPane.setMargin(node, Insets(0.0, 0.0, 0.0, 20.0))
            }
            addRow(0, "Сумма в начале срока", outputInitialSum)
            addRow(1, "Сумма в конце срока", outputSum)
            addRow(2, "Доход", outputDelta)
            addRow(3, "Эффективная ставка", outputEar)
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
    }

    private fun onCalculateButtonPressed() {
        val deposit = buildDepositFromInputs()
        log.info("Calculating for deposit: {}", deposit)
        val depositInfo = calculateProfit(deposit)
        updateOutputs(depositInfo)
    }

    private fun buildDepositFromInputs(): Deposit {
        val amountRaw = BigDecimal(inputSum.text.replace(',', '.'))
            .multiply(BigDecimal(100))
            .setScale(2)
            .toBigInteger()
        return Deposit(
            sum = Amount.create(amountRaw),
            rate = Rate.create(inputRate.text),
            periodMonths = inputPeriodMonths.text.toInt(),
            capitalization = Capitalization.fromValue(inputCapitalization.value.toString()),
            openDate = inputOpenDate.value
        )
    }

    private fun updateOutputs(depositInfo: DepositDetails) {
        outputInitialSum.text = depositInfo.startSum.toString()
        outputSum.text = depositInfo.endSum.toString()
        outputDelta.text = depositInfo.delta.toString()
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
                    amount = amount.toString(),
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

    private companion object {
        private val log = LoggerFactory.getLogger(MainView::class.java)
    }
}
