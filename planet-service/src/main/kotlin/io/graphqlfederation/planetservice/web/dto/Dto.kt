package io.graphqlfederation.planetservice.web.dto

import io.graphqlfederation.planetservice.model.Planet

class PlanetDto(
    val id: Long,
    val name: String,
    val type: Planet.Type,
    val params: ParamsDto
)

open class ParamsDto(
    val id: Long,
    val meanRadius: Double = 0.0,
    val earthsMass: Double = 0.0
)

class InhabitedPlanetParamsDto(
    id: Long,
    meanRadius: Double,
    earthsMass: Double,
    val population: Double
) : ParamsDto(id, meanRadius, earthsMass)

class UninhabitedPlanetParamsDto(
    id: Long,
    meanRadius: Double,
    earthsMass: Double
) : ParamsDto(id, meanRadius, earthsMass)

class ParamsInputDto(
    val meanRadius: Double,
    val earthsMass: Double,
    val population: Double
)
