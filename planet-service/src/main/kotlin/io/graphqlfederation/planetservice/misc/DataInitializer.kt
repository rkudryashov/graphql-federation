package io.graphqlfederation.planetservice.misc

import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.PlanetService
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import javax.inject.Singleton

@Singleton
class DataInitializer(
    private val planetService: PlanetService
) {

    @EventListener
    fun init(event: StartupEvent) {
        val commonMassPower = 24

        planetService.create("Mercury", Planet.Type.TERRESTRIAL_PLANET, 2439.7, 0.330, commonMassPower)
        planetService.create("Venus", Planet.Type.TERRESTRIAL_PLANET, 6051.8, 4.87, commonMassPower)
        planetService.create("Earth", Planet.Type.TERRESTRIAL_PLANET, 6371.0, 5.97, commonMassPower, 7.53)
        planetService.create("Mars", Planet.Type.TERRESTRIAL_PLANET, 3389.5, 0.642, commonMassPower)
        planetService.create("Jupiter", Planet.Type.GAS_GIANT, 69911.0, 1898.0, commonMassPower)
        planetService.create("Saturn", Planet.Type.GAS_GIANT, 58232.0, 568.0, commonMassPower)
        planetService.create("Uranus", Planet.Type.ICE_GIANT, 25362.0, 86.8, commonMassPower)
        planetService.create("Neptune", Planet.Type.ICE_GIANT, 24622.0, 102.0, commonMassPower)
    }
}
