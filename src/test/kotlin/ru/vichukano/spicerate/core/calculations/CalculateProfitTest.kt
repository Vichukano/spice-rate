package ru.vichukano.spicerate.core.calculations

import org.junit.jupiter.api.Test
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.Deposit
import ru.vichukano.spicerate.core.model.Rate
import java.math.BigInteger
import java.time.LocalDate
import kotlin.test.assertEquals

class CalculateProfitTest {
    private val testTarget = SimpleDepositCalculator()

    @Test
    fun `should calculate deposit without capitalization`() {
        //given
        val simpleDepositOne = Deposit(
            sum = Amount.create(100500L, 0),
            openDate = LocalDate.of(2025, 7, 6),
            periodMonths = 12,
            rate = Rate.create("13,3"),
            capitalization = Capitalization.NONE
        )
        val simpleDepositTwo = Deposit(
            sum = Amount.create(200100L, 0),
            openDate = LocalDate.of(2025, 7, 6),
            periodMonths = 33,
            rate = Rate.create("26,13"),
            capitalization = Capitalization.NONE
        )
        val simpleDepositThree = Deposit(
            sum = Amount.create(BigInteger("33333300")),
            openDate = LocalDate.of(2025, 7, 6),
            periodMonths = 3,
            rate = Rate.create("10,99"),
            capitalization = Capitalization.NONE
        )
        //when
        val resultOne = testTarget.calculateProfit(simpleDepositOne)
        val resultTwo = testTarget.calculateProfit(simpleDepositTwo)
        val resultThree = testTarget.calculateProfit(simpleDepositThree)
        //then
        assertEquals("13366,50", resultOne.delta.toString())
        assertEquals("113866,50", resultOne.endSum.toString())
        assertEquals("13,30%", resultOne.baseRate.toString().toString())
        assertEquals("13,30%", resultOne.effectiveRate.toString())
        assertEquals(LocalDate.of(2026, 7, 6), resultOne.endDate)

        assertEquals("143927,95", resultTwo.delta.toString())
        assertEquals("344027,95", resultTwo.endSum.toString())

        assertEquals("9233,59", resultThree.delta.toString())
        assertEquals("342566,59", resultThree.endSum.toString())
    }
}