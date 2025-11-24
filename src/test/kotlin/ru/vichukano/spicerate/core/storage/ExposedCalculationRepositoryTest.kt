package ru.vichukano.spicerate.core.storage

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.core.model.Rate
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.assertTrue

class ExposedCalculationRepositoryTest {
    private val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    private val repository = ExposedCalculationRepository(db)

    @BeforeEach
    fun reset() {
        transaction(db) {
            SchemaUtils.drop(DailyStatisticTable, StatisticTable, CalculationTable)
            SchemaUtils.create(CalculationTable, StatisticTable, DailyStatisticTable)
        }
    }

    @Test
    fun `should save and find by id`() {
        val id = UUID.randomUUID()
        val details = randomDetails(id)

        repository.save(details)
        val found = repository.findById(id)

        assertNotNull(found)
        assertEquals(details, found)
    }

    @Test
    fun `should return null when not found`() {
        val found = repository.findById(UUID.randomUUID())

        assertNull(found)
    }

    @Test
    fun `should find all`() {
        val details1 = randomDetails(UUID.randomUUID())
        val details2 = randomDetails(UUID.randomUUID())
        repository.save(details1)
        repository.save(details2)

        val found = repository.findAll()

        assertEquals(2, found.size)
        assertTrue(found.containsAll(listOf(details1, details2)))
    }

    @Test
    fun `should return empty list when no records`() {
        val found = repository.findAll()

        assertTrue(found.isEmpty())
    }

    @Test
    fun `should delete by id`() {
        val id = UUID.randomUUID()
        val details = randomDetails(id)
        repository.save(details)

        repository.deleteById(id)
        val found = repository.findById(id)

        assertNull(found)
    }

    @Test
    fun `should delete all`() {
        val details1 = randomDetails(UUID.randomUUID())
        val details2 = randomDetails(UUID.randomUUID())
        repository.save(details1)
        repository.save(details2)

        repository.deleteAll()
        val found = repository.findAll()

        assertTrue(found.isEmpty())
    }

    private fun randomDetails(id: UUID) = DepositDetails(
        id = id,
        //Truncate nano/micro seconds because of db precision
        createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        startSum = Amount.create(100000L),
        endSum = Amount.create(110000L),
        profit = Amount.create(10000L),
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusYears(1),
        baseRate = Rate.create(BigDecimal("0.1000")),
        termInMonths = 12,
        effectiveRate = Rate.create(BigDecimal("0.1050")),
        capitalization = Capitalization.YEAR,
        dailyStatistics = mapOf(LocalDate.now() to Amount.create(100L)),
        statistics = mapOf(LocalDate.now() to Amount.create(1000L))
    )

}
