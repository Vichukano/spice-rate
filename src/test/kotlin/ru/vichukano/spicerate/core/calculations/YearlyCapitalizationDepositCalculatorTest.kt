package ru.vichukano.spicerate.core.calculations

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.vichukano.spicerate.core.calculations.deposit.YearlyCapitalizationDepositCalculator
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.DepositRequest
import ru.vichukano.spicerate.core.model.Rate
import java.time.LocalDate

class YearlyCapitalizationDepositCalculatorTest {
    private val testTarget = YearlyCapitalizationDepositCalculator

    @Test
    fun `should calculate yearly capitalization`() {
        //given
        val depositRequest = DepositRequest(
            sum = Amount.create(100500L, 0),
            openDate = LocalDate.of(2025, 7, 16),
            termInMonths = 43,
            rate = Rate.create("13,33"),
            capitalization = Capitalization.YEAR
        )
        //when
        val result = testTarget.calculateProfit(depositRequest)
        //then
        assertEquals("157770,32", result.endSum.toString())
        assertEquals("57270,32", result.profit.toString())
        assertEquals(4, result.statistics.size)
        assertEquals("15,79%", result.effectiveRate.toString())
    }
}
