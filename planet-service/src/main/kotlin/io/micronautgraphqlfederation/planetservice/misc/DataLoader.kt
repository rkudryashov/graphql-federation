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
        createPlanet("Mercury", Planet.Type.TERRESTRIAL_PLANET, 2439.7, 0.055274)
        createPlanet("Venus", Planet.Type.TERRESTRIAL_PLANET, 6051.8, 0.815)
        createPlanet("Earth", Planet.Type.TERRESTRIAL_PLANET, 6371.0, 1.0)
        createPlanet("Mars", Planet.Type.TERRESTRIAL_PLANET, 3389.5, 0.107)
        createPlanet("Jupiter", Planet.Type.GAS_GIANT, 69911.0, 317.8)
        createPlanet("Saturn", Planet.Type.GAS_GIANT, 58232.0, 95.2)
        createPlanet("Uranus", Planet.Type.ICE_GIANT, 25362.0, 14.54)
        createPlanet("Neptune", Planet.Type.ICE_GIANT, 24622.0, 17.147)
    }

    private fun createPlanet(
        name: String,
        type: Planet.Type,
        meanRadius: Double,
        earthsMass: Double
    ) {
        val planet = Planet(
            id = 0,
            name = name,
            type = type
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