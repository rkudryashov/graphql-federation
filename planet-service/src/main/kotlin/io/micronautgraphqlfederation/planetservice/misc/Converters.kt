package io.micronautgraphqlfederation.planetservice.misc

import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.web.dto.CharacteristicsDto
import io.micronautgraphqlfederation.planetservice.web.dto.PlanetDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class PlanetConverter : GenericConverter<Planet, PlanetDto> {
    override fun toDto(entity: Planet): PlanetDto =
        PlanetDto(
            id = entity.id,
            name = entity.name,
            characteristics = null
        )
}

@Singleton
class CharacteristicsConverter : GenericConverter<Characteristics, CharacteristicsDto> {
    override fun toDto(entity: Characteristics): CharacteristicsDto =
        CharacteristicsDto(
            meanRadius = entity.meanRadius,
            earthsMass = entity.earthsMass
        )
}