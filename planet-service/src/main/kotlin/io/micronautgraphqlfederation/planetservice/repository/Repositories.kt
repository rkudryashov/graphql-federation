package io.micronautgraphqlfederation.planetservice.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import io.micronautgraphqlfederation.planetservice.model.Planet

//todo move to class
@Repository
interface PlanetRepository : CrudRepository<Planet, Long>