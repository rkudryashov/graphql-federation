package io.micronautgraphqlfederation.satelliteservice.web.dto

class SatelliteDto(
    val id: Long,
    val name: String
)

class PlanetDto(
    val id: Long,
    val satellites: List<SatelliteDto>
)