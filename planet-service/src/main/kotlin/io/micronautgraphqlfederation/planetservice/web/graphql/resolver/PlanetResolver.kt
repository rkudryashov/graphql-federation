package io.micronautgraphqlfederation.planetservice.web.graphql.resolver

import io.micronautgraphqlfederation.planetservice.misc.CharacteristicsConverter
import io.micronautgraphqlfederation.planetservice.service.CharacteristicsService
import io.micronautgraphqlfederation.planetservice.web.dto.CharacteristicsDto
import io.micronautgraphqlfederation.planetservice.web.dto.PlanetDto
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class PlanetResolver(
    private val characteristicsService: CharacteristicsService,
    private val characteristicsConverter: CharacteristicsConverter
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun characteristics(planetDto: PlanetDto): CharacteristicsDto {
        log.info("Resolve `characteristics` field for planet: $planetDto")
        return characteristicsConverter.toDto(characteristicsService.getByPlanetId(planetDto.id))
    }
}