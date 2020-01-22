package io.micronautgraphqlfederation.satelliteservice.misc

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronautgraphqlfederation.satelliteservice.model.Satellite
import io.micronautgraphqlfederation.satelliteservice.repository.SatelliteRepository
import javax.inject.Singleton

@Singleton
class DataLoader(
    private val repository: SatelliteRepository
) {
    @EventListener
    fun init(event: StartupEvent?) {
        // todo init satellites
        val cities = listOf("Buenos Aires", "Auckland", "Wellington", "Christchurch", "Johannesburg")
        cities.map { name -> Satellite(0, name) }
            .forEach { Satellite: Satellite -> repository.save(Satellite) }
    }
}