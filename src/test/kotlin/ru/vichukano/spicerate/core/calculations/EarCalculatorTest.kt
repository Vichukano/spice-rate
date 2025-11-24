package ru.vichukano.spicerate.core.calculations

import org.junit.jupiter.api.Test
import ru.vichukano.spicerate.core.calculations.deposit.EarCalculator
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.Rate
import kotlin.test.assertEquals

class EarCalculatorTest {

    @Test
    fun `should calculate ear for daily`() {
        //given
        val baseRate = Rate.create("13,3")
        val months = 12
        val capPeriod = Capitalization.DAY
        //when
        val ear = EarCalculator.ear(baseRate, capPeriod, months)
        //then
        assertEquals("14,22%", ear.toString())
    }

    @Test
    fun `should calculate ear for monthly`() {
        //given
        val baseRate = Rate.create("11,11")
        val months = 35
        val capPeriod = Capitalization.MONTH
        //when
        val ear = EarCalculator.ear(baseRate, capPeriod, months)
        //then
        assertEquals("13,05%", ear.toString())
    }

    @Test
    fun `should calculate ear for yearly`() {
        //given
        val baseRate = Rate.create("13,33")
        val months = 43
        val capPeriod = Capitalization.YEAR
        //when
        val ear = EarCalculator.ear(baseRate, capPeriod, months)
        //then
        assertEquals("15,79%", ear.toString())
    }

    @Test
    fun `should calculate ear without capitalization`() {
        //given
        val baseRate = Rate.create("32,10")
        val months = 72
        val capPeriod = Capitalization.NONE
        //when
        val ear = EarCalculator.ear(baseRate, capPeriod, months)
        //then
        assertEquals("32,10%", ear.toString())
    }
}
