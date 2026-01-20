package ru.vichukano.spicerate.core.storage

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import ru.vichukano.spicerate.core.model.Amount
import ru.vichukano.spicerate.core.model.Capitalization
import ru.vichukano.spicerate.core.model.DepositDetails
import ru.vichukano.spicerate.core.model.Replenishment
import ru.vichukano.spicerate.core.model.Rate
import java.time.LocalDate
import java.util.*

class ExposedCalculationRepository(private val db: Database) : CalculationRepository {

    init {
        transaction(db) {
            SchemaUtils.create(CalculationTable, StatisticTable, DailyStatisticTable, ReplenishmentTable)
        }
    }

    override fun update(details: DepositDetails) {
        transaction(db) {
            CalculationTable.update({ CalculationTable.id eq details.id }) {
                it[startSum] = details.startSum.minimalUnits().toLong()
                it[baseRate] = details.baseRate.decimalValue()
                it[termInMonths] = details.termInMonths
                it[startDate] = details.startDate
                it[endDate] = details.endDate
                it[capitalization] = details.capitalization.name
                it[description] = details.description.take(256)
                it[endSum] = details.endSum.minimalUnits().toLong()
                it[profit] = details.profit.minimalUnits().toLong()
                it[effectiveRate] = details.effectiveRate.decimalValue()
                it[createdAt] = details.createdAt
            }
            StatisticTable.deleteWhere { StatisticTable.calculationId eq details.id }
            details.statistics.forEach { (date, amount) ->
                StatisticTable.insert {
                    it[StatisticTable.calculationId] = details.id
                    it[StatisticTable.date] = date
                    it[StatisticTable.amount] = amount.minimalUnits().toLong()
                }
            }
            DailyStatisticTable.deleteWhere { DailyStatisticTable.calculationId eq details.id }
            details.dailyStatistics.forEach { (date, amount) ->
                DailyStatisticTable.insert {
                    it[DailyStatisticTable.calculationId] = details.id
                    it[DailyStatisticTable.date] = date
                    it[DailyStatisticTable.amount] = amount.minimalUnits().toLong()
                }
            }
            ReplenishmentTable.deleteWhere { ReplenishmentTable.calculationId eq details.id }
            details.replenishments.forEach { replenishment ->
                ReplenishmentTable.insert {
                    it[ReplenishmentTable.calculationId] = details.id
                    it[ReplenishmentTable.date] = replenishment.date
                    it[ReplenishmentTable.amount] = replenishment.sum.minimalUnits().toLong()
                }
            }
        }.also {
            log.info("Updated DepositDetails with id: {}", details.id)
            log.debug("Updated DepositDetails: {}", details)
        }
    }

    override fun save(details: DepositDetails) {
        transaction(db) {
            val calculationId = CalculationTable.insert {
                it[id] = details.id
                it[startSum] = details.startSum.minimalUnits().toLong()
                it[baseRate] = details.baseRate.decimalValue()
                it[termInMonths] = details.termInMonths
                it[startDate] = details.startDate
                it[endDate] = details.endDate
                it[capitalization] = details.capitalization.name
                it[description] = details.description.take(256)
                it[endSum] = details.endSum.minimalUnits().toLong()
                it[profit] = details.profit.minimalUnits().toLong()
                it[effectiveRate] = details.effectiveRate.decimalValue()
                it[createdAt] = details.createdAt
            } get CalculationTable.id
            details.statistics.forEach { (date, amount) ->
                StatisticTable.insert {
                    it[StatisticTable.calculationId] = calculationId
                    it[StatisticTable.date] = date
                    it[StatisticTable.amount] = amount.minimalUnits().toLong()
                }
            }
            details.dailyStatistics.forEach { (date, amount) ->
                DailyStatisticTable.insert {
                    it[DailyStatisticTable.calculationId] = calculationId
                    it[DailyStatisticTable.date] = date
                    it[DailyStatisticTable.amount] = amount.minimalUnits().toLong()
                }
            }
            details.replenishments.forEach { replenishment ->
                ReplenishmentTable.insert {
                    it[ReplenishmentTable.calculationId] = calculationId
                    it[ReplenishmentTable.date] = replenishment.date
                    it[ReplenishmentTable.amount] = replenishment.sum.minimalUnits().toLong()
                }
            }
        }.also {
            log.info("Saved DepositDetails with id: {}", details.id)
            log.debug("Saved DepositDetails: {}", details)
        }
    }

    override fun findAll(): List<DepositDetails> {
        return transaction(db) {
            CalculationTable.selectAll().map {
                val id = it[CalculationTable.id]
                val dailyStatistics = DailyStatisticTable.select { DailyStatisticTable.calculationId eq id }
                    .associate { st -> st[DailyStatisticTable.date] to Amount.create(st[DailyStatisticTable.amount]) }
                val statistics = StatisticTable.select { StatisticTable.calculationId eq id }
                    .associate { st -> st[StatisticTable.date] to Amount.create(st[StatisticTable.amount]) }
                val replenishments = ReplenishmentTable.select { ReplenishmentTable.calculationId eq id }
                    .map { replenishment ->
                        Replenishment(
                            sum = Amount.create(replenishment[ReplenishmentTable.amount]),
                            date = replenishment[ReplenishmentTable.date]
                        )
                    }
                toDetails(
                    id = id,
                    row = it,
                    dailyStatistics = dailyStatistics,
                    statistics = statistics,
                    replenishments = replenishments
                )
            }
        }
    }

    override fun findById(id: UUID): DepositDetails? {
        return transaction(db) {
            CalculationTable.select { CalculationTable.id eq id }
                .limit(1)
                .map {
                    val dailyStatistics = DailyStatisticTable.select { DailyStatisticTable.calculationId eq id }
                        .associate { st -> st[DailyStatisticTable.date] to Amount.create(st[DailyStatisticTable.amount]) }
                    val statistics = StatisticTable.select { StatisticTable.calculationId eq id }
                        .associate { st -> st[StatisticTable.date] to Amount.create(st[StatisticTable.amount]) }
                    val replenishments = ReplenishmentTable.select { ReplenishmentTable.calculationId eq id }
                        .map { replenishment ->
                            Replenishment(
                                sum = Amount.create(replenishment[ReplenishmentTable.amount]),
                                date = replenishment[ReplenishmentTable.date]
                            )
                        }
                    toDetails(
                        id = id,
                        row = it,
                        dailyStatistics = dailyStatistics,
                        statistics = statistics,
                        replenishments = replenishments
                    )
                }.singleOrNull()
        }
    }

    override fun deleteById(id: UUID) {
        transaction(db) {
            DailyStatisticTable.deleteWhere { DailyStatisticTable.calculationId eq id }
            StatisticTable.deleteWhere { StatisticTable.calculationId eq id }
            ReplenishmentTable.deleteWhere { ReplenishmentTable.calculationId eq id }
            CalculationTable.deleteWhere { CalculationTable.id eq id }
        }.also { log.info("Deleted DepositDetails with id: {}", id) }
    }

    override fun deleteAll() {
        transaction(db) {
            DailyStatisticTable.deleteAll()
            StatisticTable.deleteAll()
            ReplenishmentTable.deleteAll()
            CalculationTable.deleteAll()
        }.also { log.info("Deleted all DepositDetails") }
    }

    private fun toDetails(
        id: UUID,
        row: ResultRow,
        dailyStatistics: Map<LocalDate, Amount>,
        statistics: Map<LocalDate, Amount>,
        replenishments: List<Replenishment>
    ): DepositDetails = DepositDetails(
        id = id,
        createdAt = row[CalculationTable.createdAt],
        startSum = Amount.create(row[CalculationTable.startSum]),
        endSum = Amount.create(row[CalculationTable.endSum]),
        profit = Amount.create(row[CalculationTable.profit]),
        startDate = row[CalculationTable.startDate],
        endDate = row[CalculationTable.endDate],
        baseRate = Rate.create(row[CalculationTable.baseRate]),
        termInMonths = row[CalculationTable.termInMonths],
        effectiveRate = Rate.create(row[CalculationTable.effectiveRate]),
        capitalization = Capitalization.valueOf(row[CalculationTable.capitalization]),
        description = row[CalculationTable.description],
        dailyStatistics = dailyStatistics,
        statistics = statistics,
        replenishments = replenishments
    )

    private companion object {
        private val log = LoggerFactory.getLogger(ExposedCalculationRepository::class.java)
    }
}
