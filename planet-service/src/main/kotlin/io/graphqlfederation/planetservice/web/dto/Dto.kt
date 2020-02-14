package io.graphqlfederation.planetservice.web.dto

import io.graphqlfederation.planetservice.model.Planet
import java.math.BigDecimal

class PlanetDto(
    val id: Long,
    val name: String,
    val type: Planet.Type,
    val params: ParamsDto
)

open class ParamsDto(
    val id: Long,
    val meanRadius: Double = 0.0,
    val mass: BigDecimal = BigDecimal.ZERO
)

class InhabitedPlanetParamsDto(
    id: Long,
    meanRadius: Double,
    mass: BigDecimal,
    val population: Double
) : ParamsDto(id, meanRadius, mass)

class UninhabitedPlanetParamsDto(
    id: Long,
    meanRadius: Double,
    mass: BigDecimal
) : ParamsDto(id, meanRadius, mass)

class ParamsInputDto(
    val meanRadius: Double,
    val mass: MassInputDto,
    val population: Double?
)

class MassInputDto(
    val number: Double,
    val tenPower: Int
)
