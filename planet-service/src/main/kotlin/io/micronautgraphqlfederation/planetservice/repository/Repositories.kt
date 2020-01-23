package io.micronautgraphqlfederation.planetservice.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.model.Planet

@Repository
interface PlanetRepository : CrudRepository<Planet, Long>

@Repository
interface CharacteristicsRepository : CrudRepository<Characteristics, Long> {
    fun findByIdInList(ids: List<Long>): List<Characteristics>
}