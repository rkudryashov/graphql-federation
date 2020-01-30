package io.graphqlfederation.satelliteservice.service

import io.graphqlfederation.satelliteservice.model.Satellite
import io.graphqlfederation.satelliteservice.repository.SatelliteRepository
import io.micronaut.security.utils.SecurityService
import javax.inject.Singleton

@Singleton
class SatelliteService(
    private val repository: SatelliteRepository,
    private val securityService: SecurityService
) {

    fun create(name: String, lifeExists: Satellite.LifeExists, planetId: Long) = repository.save(
        Satellite(
            name = name,
            lifeExists = lifeExists,
            planetId = planetId
        )
    )

    fun getAll(): Iterable<Satellite> = repository.findAll()

    fun getById(id: Long): Satellite =
        repository.findById(id).orElseThrow { RuntimeException("Can't find satellite by id=$id") }

    fun getByPlanetId(planetId: Long): List<Satellite> = repository.findByPlanetId(planetId)

    fun getLifeExists(id: Long): Satellite.LifeExists {
        val userIsAuthenticated = securityService.isAuthenticated
        if (userIsAuthenticated) {
            return repository.findById(id)
                .orElseThrow { RuntimeException("Can't find satellite by id=$id") }
                .lifeExists
        } else {
            throw RuntimeException("Can't get `lifeExists` property")
        }
    }
}
