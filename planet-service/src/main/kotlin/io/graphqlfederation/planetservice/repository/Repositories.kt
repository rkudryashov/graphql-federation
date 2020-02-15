package io.graphqlfederation.planetservice.repository

import io.graphqlfederation.planetservice.model.Details
import io.graphqlfederation.planetservice.model.Planet
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface PlanetRepository : CrudRepository<Planet, Long> {
    fun findByName(name: String): Planet?
}

@Repository
interface DetailsRepository : CrudRepository<Details, Long> {
    fun findByIdInList(ids: List<Long>): List<Details>
}
