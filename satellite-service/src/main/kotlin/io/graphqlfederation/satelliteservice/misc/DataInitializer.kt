package io.graphqlfederation.satelliteservice.misc

import io.graphqlfederation.satelliteservice.model.Satellite.LifeExists.NO_DATA
import io.graphqlfederation.satelliteservice.model.Satellite.LifeExists.OPEN_QUESTION
import io.graphqlfederation.satelliteservice.service.SatelliteService
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import java.time.LocalDate
import java.time.Month
import javax.inject.Singleton

@Singleton
class DataInitializer(
    private val satelliteService: SatelliteService
) {

    @EventListener
    fun init(event: StartupEvent?) {
        satelliteService.create("Moon", OPEN_QUESTION, 3L, LocalDate.of(1959, Month.SEPTEMBER, 13))
        satelliteService.create("Phobos", NO_DATA, 4L)
        satelliteService.create("Deimos", NO_DATA, 4L)
        satelliteService.create("Io", OPEN_QUESTION, 5L)
        satelliteService.create("Europa", OPEN_QUESTION, 5L)
        satelliteService.create("Ganymede", OPEN_QUESTION, 5L)
        satelliteService.create("Callisto", OPEN_QUESTION, 5L)
        satelliteService.create("Titan", OPEN_QUESTION, 6L)
        satelliteService.create("Ariel", NO_DATA, 7L)
        satelliteService.create("Umbriel", NO_DATA, 7L)
        satelliteService.create("Titania", NO_DATA, 7L)
        satelliteService.create("Oberon", NO_DATA, 7L)
        satelliteService.create("Miranda", NO_DATA, 7L)
        satelliteService.create("Triton", NO_DATA, 8L)
    }
}