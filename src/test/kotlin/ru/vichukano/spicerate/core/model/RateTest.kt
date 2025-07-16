package ru.vichukano.spicerate.core.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class RateTest {

    @Test
    fun `should construct rate`() {
        //given
        val rawRateValueWithDot = "13.33"
        val rawRateValueWithComa = "13,33"
        val expected = BigDecimal("0.1333")
        //when
        val fromDot = Rate.create(rawRateValueWithDot)
        val fromComa = Rate.create(rawRateValueWithComa)
        //then
        assertEquals(expected, fromDot.decimalValue())
        assertEquals(expected, fromComa.decimalValue())
    }

    @Test
    fun `should throw exception if input len is wrong`() {
        //given
        val rawWrong = "13.333"
        //when then
        assertThrows<IllegalArgumentException> { Rate.create(rawWrong) }
    }

}