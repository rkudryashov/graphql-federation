package io.micronautgraphqlfederation.countryservice.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import io.micronautgraphqlfederation.countryservice.model.Country

//todo move to class
@Repository
interface CountryRepository : CrudRepository<Country, Long>