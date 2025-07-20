package ru.vichukano.spicerate.gui

import javafx.scene.control.TextField

class DoubleValueTextField : TextField() {

    init {
        textProperty().addListener { _, oldValue, newValue ->
            if (newValue.isNotEmpty() && !newValue.matches(AMOUNT_REGEX)) {
                text = oldValue
            }
        }
        focusedProperty().addListener { _, _, hasFocus ->
            if (!hasFocus) {
                val amount = getAmount()
                text = "%.2f".format(amount)
            }
        }
    }

    private fun getAmount(): Double = when {
        text.isEmpty() -> 0.0
        text.isBlank() -> 0.0
        text == null -> 0.0
        else -> text.replace(",", ".")
            .toDoubleOrNull() ?: 0.0
    }

    private companion object {
        private val AMOUNT_REGEX = Regex("^[\\d\\s]*([.,]\\d{0,2})?$")
    }
}