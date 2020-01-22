package io.micronautgraphqlfederation.planetservice.web.dto

import io.micronautgraphqlfederation.planetservice.model.Planet

data class PlanetDto(
    val id: Long,
    val name: String,
    val type: Planet.Type,
    val characteristics: CharacteristicsDto?
)

data class CharacteristicsDto(
    val meanRadius: Double,
    val earthsMass: Double
)