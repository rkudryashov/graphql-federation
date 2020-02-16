package io.graphqlfederation.planetservice.service

import io.graphqlfederation.planetservice.model.Details
import io.graphqlfederation.planetservice.model.InhabitedPlanetDetails
import io.graphqlfederation.planetservice.model.UninhabitedPlanetDetails
import io.graphqlfederation.planetservice.repository.DetailsRepository
import java.math.BigDecimal
import javax.inject.Singleton

@Singleton
class DetailsService(
    private val repository: DetailsRepository
) {

    fun create(meanRadius: Double, mass: BigDecimal, population: Double?) = (if (population == null) UninhabitedPlanetDetails(
        meanRadius = meanRadius,
        mass = mass
    ) else InhabitedPlanetDetails(
        meanRadius = meanRadius,
        mass = mass,
        population = population
    )).also { repository.save(it) }

    fun getByIds(ids: List<Long>): List<Details> = repository.findByIdInList(ids)
}
