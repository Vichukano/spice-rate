package ru.vichukano.spicerate.core.model

enum class Capitalization(val value: String) {
    NONE("Без капитализации"),
    DAY("Ежедневная"),
    MONTH("Ежемесячная"),
    YEAR("Ежегодная"),
    ;

    companion object {

        fun fromValue(value: String): Capitalization {
            return entries.firstOrNull { it.value == value } ?: NONE
        }

    }
}
