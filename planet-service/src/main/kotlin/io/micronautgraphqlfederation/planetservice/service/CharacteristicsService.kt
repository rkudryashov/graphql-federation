package io.micronautgraphqlfederation.planetservice.service

import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.repository.CharacteristicsRepository
import javax.inject.Singleton

@Singleton
class CharacteristicsService(
    private val repository: CharacteristicsRepository
) {

    fun getByPlanetId(planetId: Long): Characteristics =
        repository.findByPlanetId(planetId).orElseThrow { RuntimeException("Can't find characteristics by planetId=$planetId") }
}