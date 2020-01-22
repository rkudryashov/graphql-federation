package io.micronautgraphqlfederation.planetservice.misc

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.repository.PlanetRepository
import javax.inject.Singleton

@Singleton
class DataLoader(
    private val repository: PlanetRepository
) {
    @EventListener
    fun init(event: StartupEvent?) {
        val planets = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")
        planets.map { name -> Planet(0, name) }
            .forEach { Planet: Planet -> repository.save(Planet) }
    }
}