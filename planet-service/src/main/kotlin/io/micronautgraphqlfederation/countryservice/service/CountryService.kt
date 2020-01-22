package io.micronautgraphqlfederation.countryservice.service

import io.micronautgraphqlfederation.countryservice.model.Country
import io.micronautgraphqlfederation.countryservice.repository.CountryRepository
import javax.inject.Singleton

@Singleton
class CountryService(
    private val repository: CountryRepository
) {

    fun getAll(): Iterable<Country> = repository.findAll()

    fun getById(id: Long): Country =
        repository.findById(id).orElseThrow { RuntimeException("Can't find country by id=$id") }
}