package io.micronautgraphqlfederation.planetservice.misc

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.service.PlanetService
import javax.inject.Singleton

@Singleton
class DataInitializer(
    private val planetService: PlanetService
) {

    @EventListener
    fun init(event: StartupEvent) {
        planetService.create("Mercury", Planet.Type.TERRESTRIAL_PLANET, 2439.7, 0.055274)
        planetService.create("Venus", Planet.Type.TERRESTRIAL_PLANET, 6051.8, 0.815)
        planetService.create("Earth", Planet.Type.TERRESTRIAL_PLANET, 6371.0, 1.0, 7.53)
        planetService.create("Mars", Planet.Type.TERRESTRIAL_PLANET, 3389.5, 0.107)
        planetService.create("Jupiter", Planet.Type.GAS_GIANT, 69911.0, 317.8)
        planetService.create("Saturn", Planet.Type.GAS_GIANT, 58232.0, 95.2)
        planetService.create("Uranus", Planet.Type.ICE_GIANT, 25362.0, 14.54)
        planetService.create("Neptune", Planet.Type.ICE_GIANT, 24622.0, 17.147)
    }
}