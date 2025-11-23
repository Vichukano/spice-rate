package ru.vichukano.spicerate.core.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger

class AmountTest {

    @Test
    fun `should construct amount`() {
        Assertions.assertEquals(BigInteger("10050023"), Amount.create(100500, 23).minimalUnits())
        Assertions.assertEquals(BigInteger("10050009"), Amount.create(100500, 9).minimalUnits())
        Assertions.assertEquals("100500,23", Amount.create(100500, 23).toString())
        Assertions.assertEquals("100500,09", Amount.create(100500, 9).toString())
        Assertions.assertEquals(BigInteger("20010011"), Amount.create(BigInteger("20010011")).minimalUnits())
        Assertions.assertEquals("200100,01", Amount.create(BigInteger("20010001")).toString())
        Assertions.assertEquals(BigInteger("20010000"), Amount.create(BigInteger("20010000")).minimalUnits())
        Assertions.assertEquals("200100,00", Amount.create(BigInteger("20010000")).toString())
    }

    @Test
    fun `should return formatted string with thousands separator`() {
        Assertions.assertEquals("1_000,00", Amount.create(1000, 0).toFormattedString())
        Assertions.assertEquals("1_000_000,00", Amount.create(1000000, 0).toFormattedString())
        Assertions.assertEquals("123,45", Amount.create(123, 45).toFormattedString())
        Assertions.assertEquals("1_234_567,89", Amount.create(1234567, 89).toFormattedString())
        Assertions.assertEquals("100_500,23", Amount.create(100500, 23).toFormattedString())
        Assertions.assertEquals("0,00", Amount.create(0, 0).toFormattedString())
    }

    @Test
    fun `should throw exception if wrong values`() {
        assertThrows<IllegalArgumentException> { Amount.create(-100, 20) }
        assertThrows<IllegalArgumentException> { Amount.create(100, -20) }
        assertThrows<IllegalArgumentException> { Amount.create(100, 200) }
    }

}