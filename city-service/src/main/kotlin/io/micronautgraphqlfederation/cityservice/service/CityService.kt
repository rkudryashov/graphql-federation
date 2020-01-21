package io.micronautgraphqlfederation.cityservice.service

import io.micronautgraphqlfederation.cityservice.model.City
import io.micronautgraphqlfederation.cityservice.repository.CityRepository
import javax.inject.Singleton

@Singleton
class CityService(
    private val repository: CityRepository
) {

    fun getAll(): Iterable<City> = repository.findAll()

    fun getById(id: Long): City = repository.findById(id).orElseThrow { RuntimeException("Can't find city by id=$id") }
}