package io.micronautgraphqlfederation.countryservice.misc

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronautgraphqlfederation.countryservice.model.Country
import io.micronautgraphqlfederation.countryservice.repository.CountryRepository
import javax.inject.Singleton

@Singleton
class DataLoader(
    private val repository: CountryRepository
) {
    @EventListener
    fun init(event: StartupEvent?) {
        val cities = listOf("Argentina", "New Zealand", "South Africa")
        cities.map { name -> Country(0, name) }
            .forEach { country: Country -> repository.save(country) }
    }
}