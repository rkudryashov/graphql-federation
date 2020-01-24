package io.micronautgraphqlfederation.planetservice.web.dto

import io.micronautgraphqlfederation.planetservice.model.Planet

sealed class PlanetDto(
    val id: Long,
    val name: String,
    val type: Planet.Type,
    val characteristics: CharacteristicsDto
)

class InhabitedPlanetDto(
    id: Long,
    name: String,
    type: Planet.Type,
    characteristics: CharacteristicsDto,
    val population: Double
) : PlanetDto(id, name, type, characteristics)

class UninhabitedPlanetDto(
    id: Long,
    name: String,
    type: Planet.Type,
    characteristics: CharacteristicsDto
) : PlanetDto(id, name, type, characteristics)

class CharacteristicsDto(
    val id: Long,
    val meanRadius: Double = 0.0,
    val earthsMass: Double = 0.0
)

class CharacteristicsInputDto(
    val meanRadius: Double,
    val earthsMass: Double
)
