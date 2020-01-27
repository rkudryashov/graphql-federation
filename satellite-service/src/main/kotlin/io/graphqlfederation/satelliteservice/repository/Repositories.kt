package io.graphqlfederation.satelliteservice.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import io.graphqlfederation.satelliteservice.model.Satellite

//todo move to class
@Repository
interface SatelliteRepository : CrudRepository<Satellite, Long> {
    fun findByPlanetId(planetId: Long): List<Satellite>
}