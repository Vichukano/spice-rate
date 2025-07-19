package ru.vichukano.spicerate.gui

import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.text.Text
import ru.vichukano.spicerate.core.model.Capitalization

class MainController {
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
    private lateinit var outputSum: Text

    fun initialize() {
        initTextFields()
        initCapitalization()
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
    }

}