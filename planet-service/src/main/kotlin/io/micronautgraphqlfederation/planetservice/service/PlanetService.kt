package io.micronautgraphqlfederation.planetservice.service

import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.repository.PlanetRepository
import javax.inject.Singleton

@Singleton
class PlanetService(
    private val repository: PlanetRepository
) {

    fun getAll(): Iterable<Planet> = repository.findAll()

    fun getById(id: Long): Planet =
        repository.findById(id).orElseThrow { RuntimeException("Can't find Planet by id=$id") }
}