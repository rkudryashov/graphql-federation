package io.micronautgraphqlfederation.planetservice.service

import io.micronautgraphqlfederation.planetservice.model.InhabitedPlanet
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.model.UninhabitedPlanet
import io.micronautgraphqlfederation.planetservice.repository.PlanetRepository
import javax.inject.Singleton

@Singleton
class PlanetService(
    private val repository: PlanetRepository,
    private val characteristicsService: CharacteristicsService
) {

    fun getAll(): Iterable<Planet> = repository.findAll()

    fun getById(id: Long): Planet = repository
        .findById(id)
        .orElseThrow { RuntimeException("Can't find planet by id=$id") }

    fun getByName(name: String): Planet = repository.findByName(name)
        ?: throw RuntimeException("Can't find planet by name=$name")

    fun create(
        name: String,
        type: Planet.Type,
        meanRadius: Double,
        earthsMass: Double,
        population: Double? = null
    ): Planet {
        val characteristics = characteristicsService.create(meanRadius, earthsMass)

        when (population) {
            null -> UninhabitedPlanet(
                name = name,
                type = type,
                characteristicsId = characteristics.id
            )
            else -> InhabitedPlanet(
                name = name,
                type = type,
                characteristicsId = characteristics.id,
                population = population
            )
        }.also {
            return repository.save(it)
        }
    }
}