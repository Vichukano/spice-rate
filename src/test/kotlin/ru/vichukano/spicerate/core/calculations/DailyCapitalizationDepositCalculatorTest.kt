package ru.vichukano.spicerate.core.calculations

import org.junit.jupiter.api.Test
import ru.vichukano.spicerate.core.calculations.deposit.DailyCapitalizationDepositCalculator
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.DepositRequest
import ru.vichukano.spicerate.core.model.Rate
import java.time.LocalDate
import kotlin.test.assertEquals

class DailyCapitalizationDepositCalculatorTest {
    private val testTarget = DailyCapitalizationDepositCalculator

    @Test
    fun `should calculate deposit with daily capitalization`() {
        //given
        val dailyDepositRequestOne = DepositRequest(
            sum = Amount.create(100500L, 0),
            openDate = LocalDate.of(2025, 7, 6),
            termInMonths = 12,
            rate = Rate.create("13,3"),
            capitalization = Capitalization.DAY,
            description = "test",
        )
        //when
        val result = testTarget.calculateProfit(dailyDepositRequestOne)
        //then
        assertEquals("114793,34", result.endSum.toString())
        assertEquals("14293,34", result.profit.toString())
        assertEquals(365, result.statistics.size)
    }

}