package ru.vichukano.spicerate.core.calculations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.Rate
import java.time.LocalDate

class MonthlyCapitalizationDepositCalculatorTest {
    private val testTarget = MonthlyCapitalizationDepositCalculator()

    @Test
    fun `should calculate monthly capitalization`() {
        //given
        val deposit = Deposit(
            sum = Amount.create(100500L, 0),
            openDate = LocalDate.of(2025, 7, 15),
            periodMonths = 12,
            rate = Rate.create("13,3"),
            capitalization = Capitalization.MONTH
        )
        //when
        val result = testTarget.calculateProfit(deposit)
        //then
        assertEquals("114712,09", result.endSum.toString())
        assertEquals("14212,09", result.delta.toString())
        assertEquals(12, result.statistics.size)
        assertEquals("14,14%", result.effectiveRate.toString())
    }

}