package io.micronautgraphqlfederation.planetservice.misc

import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.model.InhabitedPlanetCharacteristics
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.model.UninhabitedPlanetCharacteristics
import io.micronautgraphqlfederation.planetservice.web.dto.CharacteristicsDto
import io.micronautgraphqlfederation.planetservice.web.dto.InhabitedPlanetCharacteristicsDto
import io.micronautgraphqlfederation.planetservice.web.dto.PlanetDto
import io.micronautgraphqlfederation.planetservice.web.dto.UninhabitedPlanetCharacteristicsDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class PlanetConverter : GenericConverter<Planet, PlanetDto> {
    override fun toDto(entity: Planet): PlanetDto {
        val characteristics = CharacteristicsDto(id = entity.characteristicsId)

        return PlanetDto(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            characteristics = characteristics
        )

    }
}

@Singleton
class CharacteristicsConverter : GenericConverter<Characteristics, CharacteristicsDto> {
    override fun toDto(entity: Characteristics): CharacteristicsDto = when (entity) {
        is InhabitedPlanetCharacteristics -> InhabitedPlanetCharacteristicsDto(
            id = entity.id,
            meanRadius = entity.meanRadius,
            earthsMass = entity.earthsMass,
            population = entity.population
        )
        is UninhabitedPlanetCharacteristics -> UninhabitedPlanetCharacteristicsDto(
            id = entity.id,
            meanRadius = entity.meanRadius,
            earthsMass = entity.earthsMass
        )
    }
}