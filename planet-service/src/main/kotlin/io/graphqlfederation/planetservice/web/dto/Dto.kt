package io.graphqlfederation.planetservice.web.dto

import io.graphqlfederation.planetservice.model.Planet
import java.math.BigDecimal

class PlanetDto(
    val id: Long,
    val name: String,
    val type: Planet.Type,
    val details: DetailsDto
)

open class DetailsDto(
    val id: Long,
    val meanRadius: Double = 0.0,
    val mass: BigDecimal = BigDecimal.ZERO
)

class InhabitedPlanetDetailsDto(
    id: Long,
    meanRadius: Double,
    mass: BigDecimal,
    val population: Double
) : DetailsDto(id, meanRadius, mass)

class UninhabitedPlanetDetailsDto(
    id: Long,
    meanRadius: Double,
    mass: BigDecimal
) : DetailsDto(id, meanRadius, mass)

class DetailsInputDto(
    val meanRadius: Double,
    val mass: MassInputDto,
    val population: Double?
)

class MassInputDto(
    val number: Double,
    val tenPower: Int
)
