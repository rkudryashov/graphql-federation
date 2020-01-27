package io.graphqlfederation.satelliteservice.service

import io.graphqlfederation.satelliteservice.model.Satellite
import io.graphqlfederation.satelliteservice.repository.SatelliteRepository
import javax.inject.Singleton

@Singleton
class SatelliteService(
    private val repository: SatelliteRepository
) {

    fun getAll(): Iterable<Satellite> = repository.findAll()

    fun getById(id: Long): Satellite =
        repository.findById(id).orElseThrow { RuntimeException("Can't find satellite by id=$id") }

    fun getByPlanetId(planetId: Long): List<Satellite> = repository.findByPlanetId(planetId)
}