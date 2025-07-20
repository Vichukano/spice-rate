package ru.vichukano.spicerate.gui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.text.Text
import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.calculations.*
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.Rate
import java.math.BigDecimal
import java.time.LocalDate

class MainController {
    private val calculateProfit = CalculateProfit(
        simple = SimpleDepositCalculator(),
        daily = DailyCapitalizationDepositCalculator(),
        monthly = MonthlyCapitalizationDepositCalculator(),
        yearly = YearlyCapitalizationDepositCalculator(),
    )

    @FXML
    private lateinit var inputCapitalization: ComboBox<Any>

    @FXML
    private lateinit var inputOpenDate: DatePicker

    @FXML
    private lateinit var inputPeriodMonths: TextField

    @FXML
    private lateinit var inputRate: TextField

    @FXML
    private lateinit var inputSum: TextField

    @FXML
    private lateinit var outputDelta: Text

    @FXML
    private lateinit var outputEar: Text

    @FXML
    private lateinit var outputInitialSum: Text

    @FXML
    private lateinit var outputSum: Text


    @FXML
    private lateinit var outputStatisticsPane: TableView<TableItem>

    fun initialize() {
        initTextFields()
        initDate()
        initCapitalization()
        initStatisticsPane()
    }

    @FXML
    @Suppress("UNUSED_PARAMETER")
    fun onCalculateButtonPressed(event: ActionEvent) {
        log.info(
            "Start to calculate. Amount: ${inputSum.text}, " +
                    "Rate: ${inputRate.text}, " +
                    "Period: ${inputPeriodMonths.text}, " +
                    "Capitalization: ${inputCapitalization.value}, " +
                    "openDate: ${inputOpenDate.value}"
        )
        val amountRaw = BigDecimal(inputSum.text.replace(',', '.'))
            .multiply(BigDecimal(100))
            .setScale(2)
            .toBigInteger()
        val deposit = Deposit(
            sum = Amount.create(amountRaw),
            rate = Rate.create(inputRate.text),
            periodMonths = inputPeriodMonths.text.toInt(),
            capitalization = Capitalization.fromValue(inputCapitalization.value.toString()),
            openDate = inputOpenDate.value
        )
        log.info("Deposit: $deposit")
        val depositInfo = calculateProfit(deposit)
        with(depositInfo) {
            outputInitialSum.text = startSum.toString()
            outputSum.text = endSum.toString()
            outputDelta.text = delta.toString()
            outputEar.text = effectiveRate.toString()
            populateStatisticsPane(statistics)
        }
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

    @Suppress("UNCHECKED_CAST")
    private fun initStatisticsPane() {
        outputStatisticsPane.isVisible = false
        (outputStatisticsPane.columns[0] as TableColumn<TableItem, String>).apply {
            cellValueFactory = PropertyValueFactory("number")
        }
        (outputStatisticsPane.columns[1] as TableColumn<TableItem, String>).apply {
            cellValueFactory = PropertyValueFactory("date")
        }
        (outputStatisticsPane.columns[2] as TableColumn<TableItem, String>).apply {
            cellValueFactory = PropertyValueFactory("amount")
        }
    }

    private fun initCapitalization() {
        with(inputCapitalization) {
            value = Capitalization.NONE.value
            items.addAll(
                Capitalization.entries.map { it.value }
            )
        }
    }

    private fun initTextFields() {
        inputSum.text = "100500,00"
        inputRate.text = "10,00"
        inputPeriodMonths.text = "12"
    }

    private fun initDate() {
        inputOpenDate.value = LocalDate.now()
    }

    private companion object {
        private val log = LoggerFactory.getLogger(MainController::class.java)
    }
}