package io.micronautgraphqlfederation.cityservice.misc

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronautgraphqlfederation.cityservice.model.City
import io.micronautgraphqlfederation.cityservice.repository.CityRepository
import javax.inject.Singleton

@Singleton
class DataLoader(
    private val repository: CityRepository
) {
    @EventListener
    fun init(event: StartupEvent?) {
        val cities = listOf("Buenos Aires", "Auckland", "Wellington", "Christchurch", "Johannesburg")
        cities.map { name -> City(0, name) }
            .forEach { city: City -> repository.save(city) }
    }
}