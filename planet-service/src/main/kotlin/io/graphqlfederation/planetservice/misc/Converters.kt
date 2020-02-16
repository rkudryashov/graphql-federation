package io.graphqlfederation.planetservice.misc

import io.graphqlfederation.planetservice.model.Details
import io.graphqlfederation.planetservice.model.InhabitedPlanetDetails
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.model.UninhabitedPlanetDetails
import io.graphqlfederation.planetservice.web.dto.DetailsDto
import io.graphqlfederation.planetservice.web.dto.InhabitedPlanetDetailsDto
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import io.graphqlfederation.planetservice.web.dto.UninhabitedPlanetDetailsDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class PlanetConverter : GenericConverter<Planet, PlanetDto> {
    override fun toDto(entity: Planet): PlanetDto {
        val details = DetailsDto(id = entity.detailsId)

        return PlanetDto(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            details = details
        )
    }
}

@Singleton
class DetailsConverter : GenericConverter<Details, DetailsDto> {
    override fun toDto(entity: Details): DetailsDto = when (entity) {
        is InhabitedPlanetDetails -> InhabitedPlanetDetailsDto(
            id = entity.id,
            meanRadius = entity.meanRadius,
            mass = entity.mass,
            population = entity.population
        )
        is UninhabitedPlanetDetails -> UninhabitedPlanetDetailsDto(
            id = entity.id,
            meanRadius = entity.meanRadius,
            mass = entity.mass
        )
    }
}
