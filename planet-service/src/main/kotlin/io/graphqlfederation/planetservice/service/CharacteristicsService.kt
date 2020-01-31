package io.graphqlfederation.planetservice.service

import io.graphqlfederation.planetservice.model.Characteristics
import io.graphqlfederation.planetservice.model.InhabitedPlanetCharacteristics
import io.graphqlfederation.planetservice.model.UninhabitedPlanetCharacteristics
import io.graphqlfederation.planetservice.repository.CharacteristicsRepository
import javax.inject.Singleton

@Singleton
class CharacteristicsService(
    private val repository: CharacteristicsRepository
) {

    fun create(meanRadius: Double, earthsMass: Double, population: Double) = when (population) {
        0.0 -> UninhabitedPlanetCharacteristics(
            meanRadius = meanRadius,
            earthsMass = earthsMass
        )
        else -> InhabitedPlanetCharacteristics(
            meanRadius = meanRadius,
            earthsMass = earthsMass,
            population = population
        )
    }.also {
        repository.save(it)
    }

    fun getByIds(ids: List<Long>): List<Characteristics> = repository.findByIdInList(ids)
}