package io.micronautgraphqlfederation.planetservice.web.dto

import io.micronautgraphqlfederation.planetservice.model.Planet

class PlanetDto(
    val id: Long,
    val name: String,
    val type: Planet.Type,
    val characteristics: CharacteristicsDto
)

open class CharacteristicsDto(
    val id: Long,
    val meanRadius: Double = 0.0,
    val earthsMass: Double = 0.0
)

class InhabitedPlanetCharacteristicsDto(
    id: Long,
    meanRadius: Double,
    earthsMass: Double,
    val population: Double
) : CharacteristicsDto(id, meanRadius, earthsMass)

class UninhabitedPlanetCharacteristicsDto(
    id: Long,
    meanRadius: Double,
    earthsMass: Double
) : CharacteristicsDto(id, meanRadius, earthsMass)

class CharacteristicsInputDto(
    val meanRadius: Double,
    val earthsMass: Double,
    val population: Double
)
