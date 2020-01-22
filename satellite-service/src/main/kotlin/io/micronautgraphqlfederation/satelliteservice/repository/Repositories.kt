package io.micronautgraphqlfederation.satelliteservice.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import io.micronautgraphqlfederation.satelliteservice.model.Satellite

//todo move to class
@Repository
interface SatelliteRepository : CrudRepository<Satellite, Long>