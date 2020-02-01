package io.graphqlfederation.planetservice.service

import io.graphqlfederation.planetservice.model.Params
import io.graphqlfederation.planetservice.model.InhabitedPlanetParams
import io.graphqlfederation.planetservice.model.UninhabitedPlanetParams
import io.graphqlfederation.planetservice.repository.ParamsRepository
import javax.inject.Singleton

@Singleton
class ParamsService(
    private val repository: ParamsRepository
) {

    fun create(meanRadius: Double, earthsMass: Double, population: Double) = when (population) {
        0.0 -> UninhabitedPlanetParams(
            meanRadius = meanRadius,
            earthsMass = earthsMass
        )
        else -> InhabitedPlanetParams(
            meanRadius = meanRadius,
            earthsMass = earthsMass,
            population = population
        )
    }.also {
        repository.save(it)
    }

    fun getByIds(ids: List<Long>): List<Params> = repository.findByIdInList(ids)
}