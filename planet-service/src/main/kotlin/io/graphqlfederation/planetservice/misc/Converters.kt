package io.graphqlfederation.planetservice.misc

import io.graphqlfederation.planetservice.model.InhabitedPlanetParams
import io.graphqlfederation.planetservice.model.Params
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.model.UninhabitedPlanetParams
import io.graphqlfederation.planetservice.web.dto.InhabitedPlanetParamsDto
import io.graphqlfederation.planetservice.web.dto.ParamsDto
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import io.graphqlfederation.planetservice.web.dto.UninhabitedPlanetParamsDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class PlanetConverter : GenericConverter<Planet, PlanetDto> {
    override fun toDto(entity: Planet): PlanetDto {
        val params = ParamsDto(id = entity.paramsId)

        return PlanetDto(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            params = params
        )
    }
}

@Singleton
class ParamsConverter : GenericConverter<Params, ParamsDto> {
    override fun toDto(entity: Params): ParamsDto = when (entity) {
        is InhabitedPlanetParams -> InhabitedPlanetParamsDto(
            id = entity.id,
            meanRadius = entity.meanRadius,
            mass = entity.mass,
            population = entity.population
        )
        is UninhabitedPlanetParams -> UninhabitedPlanetParamsDto(
            id = entity.id,
            meanRadius = entity.meanRadius,
            mass = entity.mass
        )
    }
}
