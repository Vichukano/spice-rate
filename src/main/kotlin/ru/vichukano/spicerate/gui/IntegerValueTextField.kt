package ru.vichukano.spicerate.gui

import javafx.scene.control.TextField

class IntegerValueTextField : TextField() {

    init {
        textProperty().addListener { _, oldValue, newValue ->
            if (newValue.isNotEmpty() && !newValue.matches(DIGITS_REGEX)) {
                text = oldValue
            }
        }
    }

    private companion object {
        private val DIGITS_REGEX = Regex("[0-9]+")
    }
}