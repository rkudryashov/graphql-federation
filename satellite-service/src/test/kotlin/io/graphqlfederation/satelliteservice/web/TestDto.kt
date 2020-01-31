package io.graphqlfederation.satelliteservice.web

import java.time.LocalDate

class TestSatelliteDto(
    val id: Long,
    val name: String,
    val firstSpacecraftLandingDate: LocalDate?
)