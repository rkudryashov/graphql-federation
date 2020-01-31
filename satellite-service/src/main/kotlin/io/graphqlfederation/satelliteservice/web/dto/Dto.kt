package io.graphqlfederation.satelliteservice.web.dto

import io.graphqlfederation.satelliteservice.model.Satellite
import java.time.LocalDate

class SatelliteDto(
    val id: Long,
    val name: String,
    val lifeExists: Satellite.LifeExists,
    val firstSpacecraftLandingDate: LocalDate?
)

class PlanetDto(
    val id: Long,
    val satellites: List<SatelliteDto>
)