package io.micronautgraphqlfederation.planetservice.misc

import io.micronautgraphqlfederation.planetservice.model.Characteristics
import io.micronautgraphqlfederation.planetservice.model.InhabitedPlanet
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.model.UninhabitedPlanet
import io.micronautgraphqlfederation.planetservice.web.dto.CharacteristicsDto
import io.micronautgraphqlfederation.planetservice.web.dto.InhabitedPlanetDto
import io.micronautgraphqlfederation.planetservice.web.dto.PlanetDto
import io.micronautgraphqlfederation.planetservice.web.dto.UninhabitedPlanetDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class PlanetConverter : GenericConverter<Planet, PlanetDto> {
    override fun toDto(entity: Planet): PlanetDto {
        val characteristics = CharacteristicsDto(id = entity.characteristicsId)

        return when (entity) {
            is InhabitedPlanet -> InhabitedPlanetDto(
                id = entity.id,
                name = entity.name,
                type = entity.type,
                characteristics = characteristics,
                population = entity.population
            )
            is UninhabitedPlanet -> UninhabitedPlanetDto(
                id = entity.id,
                name = entity.name,
                type = entity.type,
                characteristics = characteristics
            )
        }
    }
}

@Singleton
class CharacteristicsConverter : GenericConverter<Characteristics, CharacteristicsDto> {
    override fun toDto(entity: Characteristics): CharacteristicsDto = CharacteristicsDto(
        id = entity.id,
        meanRadius = entity.meanRadius,
        earthsMass = entity.earthsMass
    )
}