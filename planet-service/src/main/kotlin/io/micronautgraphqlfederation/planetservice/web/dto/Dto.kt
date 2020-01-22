package io.micronautgraphqlfederation.planetservice.web.dto

data class PlanetDto(
    val id: Long,
    val name: String,
    val characteristics: CharacteristicsDto?
)

data class CharacteristicsDto(
    val meanRadius: Double,
    val earthsMass: Double
)