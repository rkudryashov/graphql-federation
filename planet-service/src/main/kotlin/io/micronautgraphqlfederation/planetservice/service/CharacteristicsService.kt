package io.micronautgraphqlfederation.planetservice.service

import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.repository.CharacteristicsRepository
import javax.inject.Singleton

@Singleton
class CharacteristicsService(
    private val repository: CharacteristicsRepository
) {

    fun getByIds(ids: List<Long>): List<Characteristics> = repository.findByIdInList(ids)
}