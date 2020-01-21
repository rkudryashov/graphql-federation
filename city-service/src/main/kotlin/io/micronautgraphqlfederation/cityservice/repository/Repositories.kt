package io.micronautgraphqlfederation.cityservice.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import io.micronautgraphqlfederation.cityservice.model.City

//todo move to class
@Repository
interface CityRepository : CrudRepository<City, Long>