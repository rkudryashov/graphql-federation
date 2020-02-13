package io.graphqlfederation.planetservice.service

import io.graphqlfederation.planetservice.model.InhabitedPlanetParams
import io.graphqlfederation.planetservice.model.Params
import io.graphqlfederation.planetservice.model.UninhabitedPlanetParams
import io.graphqlfederation.planetservice.repository.ParamsRepository
import java.math.BigDecimal
import javax.inject.Singleton

@Singleton
class ParamsService(
    private val repository: ParamsRepository
) {

    fun create(meanRadius: Double, mass: BigDecimal, population: Double) = when (population) {
        0.0 -> UninhabitedPlanetParams(
            meanRadius = meanRadius,
            mass = mass
        )
        else -> InhabitedPlanetParams(
            meanRadius = meanRadius,
            mass = mass,
            population = population
        )
    }.also {
        repository.save(it)
    }

    fun getByIds(ids: List<Long>): List<Params> = repository.findByIdInList(ids)
}
