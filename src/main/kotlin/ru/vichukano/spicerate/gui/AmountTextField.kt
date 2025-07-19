package ru.vichukano.spicerate.gui

import javafx.scene.control.TextField

class AmountTextField : TextField() {

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
        setOnKeyReleased {
            if (!text.contains(COMMA_REGEX) && text.matches(DIGITS_REGEX)) {
                appendText(".00")
                positionCaret(text.length - 3)
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
        private val DIGITS_REGEX = Regex("[0-9]+")
        private val COMMA_REGEX = Regex("[.,]")
    }
}