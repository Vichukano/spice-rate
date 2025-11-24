package ru.vichukano.spicerate.core.calculations

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.vichukano.spicerate.core.calculations.deposit.DepositCalculatorProvider
import ru.vichukano.spicerate.core.model.*
import ru.vichukano.spicerate.core.storage.CalculationTable
import ru.vichukano.spicerate.core.storage.DailyStatisticTable
import ru.vichukano.spicerate.core.storage.ExposedCalculationRepository
import ru.vichukano.spicerate.core.storage.StatisticTable
import java.time.LocalDate

class ReplenishDepositTest {
    private val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    private val repository = ExposedCalculationRepository(db)
    private val testTaget = ReplenishDeposit(repository)

    @BeforeEach
    fun reset() {
        transaction(db) {
            SchemaUtils.create(CalculationTable, StatisticTable, DailyStatisticTable)
        }
    }

    @Test
    fun `should correctly replenish deposit with no capitalization`() {
        //given
        val openDate = LocalDate.of(2025, 12, 7)
        val depositRequest = DepositRequest(
            sum = Amount.create(100500, 0),
            openDate = openDate,
            termInMonths = 13,
            rate = Rate.create("13.3"),
            capitalization = Capitalization.NONE,
        )
        val depositDetails = DepositCalculatorProvider.calculateProfit(depositRequest)
        repository.save(depositDetails)
        val replenishment = Replenishment(
            sum = Amount.create(5500, 0),
            replenishmentDate = openDate.plusMonths(4),
            depositId = depositDetails.id
        )
        //when
        val result: DepositDetails = testTaget(replenishment)
        //then
        Assertions.assertEquals(result.endSum, Amount.create(121052, 86))
        Assertions.assertEquals(result.profit, Amount.create(15052, 86))
        Assertions.assertEquals(result.statistics.values.last(), Amount.create(15052, 86))
    }

    @Test
    fun `should correctly replenish deposit with monthly capitalization`() {
        //given
        val openDate = LocalDate.of(2025, 12, 7)
        val depositRequest = DepositRequest(
            sum = Amount.create(200300, 0),
            openDate = openDate,
            termInMonths = 15,
            rate = Rate.create("16.77"),
            capitalization = Capitalization.MONTH,
        )
        val depositDetails = DepositCalculatorProvider.calculateProfit(depositRequest)
        repository.save(depositDetails)
        val replenishment = Replenishment(
            sum = Amount.create(100500, 0),
            replenishmentDate = openDate.plusMonths(7),
            depositId = depositDetails.id
        )
        //when
        val result: DepositDetails = testTaget(replenishment)
        //then
        Assertions.assertEquals(result.endSum, Amount.create(358798, 84))
        Assertions.assertEquals(result.profit, Amount.create(57998, 84))
    }

    @Test
    fun `should correctly replenish deposit with daily capitalization`() {
        //given
        val openDate = LocalDate.of(2025, 12, 7)
        val depositRequest = DepositRequest(
            sum = Amount.create(333333, 0),
            openDate = openDate,
            termInMonths = 11,
            rate = Rate.create("33.33"),
            capitalization = Capitalization.DAY,
        )
        val depositDetails = DepositCalculatorProvider.calculateProfit(depositRequest)
        repository.save(depositDetails)
        val replenishment = Replenishment(
            sum = Amount.create(7777, 0),
            replenishmentDate = openDate.plusMonths(3),
            depositId = depositDetails.id
        )
        //when
        val result: DepositDetails = testTaget(replenishment)
        //then
        Assertions.assertEquals(result.endSum, Amount.create(462280, 24))
        Assertions.assertEquals(result.profit, Amount.create(121170, 24))
    }

    @Test
    fun `should correctly replenish deposit with year capitalization`() {
        //given
        val openDate = LocalDate.of(2025, 12, 7)
        val depositRequest = DepositRequest(
            sum = Amount.create(224_433, 0),
            openDate = openDate,
            termInMonths = 24,
            rate = Rate.create("11,22"),
            capitalization = Capitalization.YEAR,
        )
        val depositDetails = DepositCalculatorProvider.calculateProfit(depositRequest)
        repository.save(depositDetails)
        val replenishment = Replenishment(
            sum = Amount.create(1_000_500, 0),
            replenishmentDate = openDate.plusMonths(10),
            depositId = depositDetails.id
        )
        //when
        val result: DepositDetails = testTaget(replenishment)
        //then
        Assertions.assertEquals(result.endSum, Amount.create(1411242, 75))
        Assertions.assertEquals(result.profit, Amount.create(186309, 75))
        Assertions.assertEquals(result.effectiveRate, Rate.create("11,85"))
    }
}