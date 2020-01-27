package io.graphqlfederation.satelliteservice.misc

import io.graphqlfederation.satelliteservice.model.Satellite
import io.graphqlfederation.satelliteservice.web.dto.SatelliteDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class SatelliteConverter : GenericConverter<Satellite, SatelliteDto> {
    override fun toDto(entity: Satellite): SatelliteDto = SatelliteDto(
        id = entity.id,
        name = entity.name
    )
}