package io.graphqlfederation.satelliteservice.misc

import io.graphqlfederation.satelliteservice.model.Satellite
import io.graphqlfederation.satelliteservice.repository.SatelliteRepository
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import javax.inject.Singleton

@Singleton
class DataInitializer(
    private val repository: SatelliteRepository
) {

    @EventListener
    fun init(event: StartupEvent?) {
        val planetsToSatellites = mapOf(
            3L to listOf("Moon"),
            4L to listOf("Phobos", "Deimos"),
            5L to listOf("Io", "Europa", "Ganymede", "Callisto"),
            6L to listOf("Titan"),
            7L to listOf("Ariel", "Umbriel", "Titania", "Oberon", "Miranda"),
            8L to listOf("Triton")
        )

        planetsToSatellites.forEach { pair ->
            val planetId = pair.key
            val satellites = pair.value

            satellites.map { satelliteName ->
                Satellite(
                    name = satelliteName,
                    planetId = planetId
                )
            }.forEach { satellite ->
                repository.save(satellite)
            }
        }
    }
}