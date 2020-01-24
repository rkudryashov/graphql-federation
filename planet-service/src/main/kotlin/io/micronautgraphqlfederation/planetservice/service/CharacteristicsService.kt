package io.micronautgraphqlfederation.planetservice.service

import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.repository.CharacteristicsRepository
import javax.inject.Singleton

@Singleton
class CharacteristicsService(
    private val repository: CharacteristicsRepository
) {

    fun create(meanRadius: Double, earthsMass: Double) = repository.save(
        Characteristics(
            meanRadius = meanRadius,
            earthsMass = earthsMass
        )
    )

    fun getByIds(ids: List<Long>): List<Characteristics> = repository.findByIdInList(ids)
}