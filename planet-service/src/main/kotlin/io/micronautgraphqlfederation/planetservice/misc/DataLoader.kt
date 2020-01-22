package io.micronautgraphqlfederation.planetservice.misc

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.repository.CharacteristicsRepository
import io.micronautgraphqlfederation.planetservice.repository.PlanetRepository
import javax.inject.Singleton

@Singleton
class DataLoader(
    private val planetRepository: PlanetRepository,
    private val characteristicsRepository: CharacteristicsRepository
) {

    @EventListener
    fun init(event: StartupEvent) {
        createPlanet("Mercury", 2439.7, 0.055274)
        createPlanet("Venus", 6051.8, 0.815)
        createPlanet("Earth", 6371.0, 1.0)
        createPlanet("Mars", 3389.5, 0.107)
        createPlanet("Jupiter", 69911.0, 317.8)
        createPlanet("Saturn", 58232.0, 95.2)
        createPlanet("Uranus", 25362.0, 14.54)
        createPlanet("Neptune", 24622.0, 17.147)
    }

    private fun createPlanet(name: String, meanRadius: Double, earthsMass: Double) {
        val planet = Planet(
            id = 0,
            name = name
        )

        planetRepository.save(planet)

        val characteristics = Characteristics(
            id = 0,
            planet = planet,
            meanRadius = meanRadius,
            earthsMass = earthsMass
        )

        characteristicsRepository.save(characteristics)
    }
}