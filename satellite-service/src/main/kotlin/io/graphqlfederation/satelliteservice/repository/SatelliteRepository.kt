package io.graphqlfederation.satelliteservice.repository

import io.graphqlfederation.satelliteservice.model.Satellite
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface SatelliteRepository : CrudRepository<Satellite, Long> {
    fun findByPlanetId(planetId: Long): List<Satellite>
    fun findByName(name: String): Satellite?
}