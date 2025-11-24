package ru.vichukano.spicerate.core.storage

import ru.vichukano.spicerate.core.model.DepositDetails
import java.util.UUID


interface CalculationRepository {
    fun update(details: DepositDetails)
    fun save(details: DepositDetails)
    fun findAll(): List<DepositDetails>
    fun findById(id: UUID): DepositDetails?
    fun deleteById(id: UUID)
    fun deleteAll()
}
