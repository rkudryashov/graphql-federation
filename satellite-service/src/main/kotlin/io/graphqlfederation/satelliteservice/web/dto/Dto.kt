package io.graphqlfederation.satelliteservice.web.dto

import io.graphqlfederation.satelliteservice.model.Satellite

class SatelliteDto(
    val id: Long,
    val name: String,
    val lifeExists: Satellite.LifeExists
)

class PlanetDto(
    val id: Long,
    val satellites: List<SatelliteDto>
)