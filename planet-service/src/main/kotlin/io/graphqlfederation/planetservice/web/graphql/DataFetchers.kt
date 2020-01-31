package io.graphqlfederation.planetservice.web.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.graphqlfederation.planetservice.misc.PlanetConverter
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.PlanetService
import io.graphqlfederation.planetservice.web.dto.CharacteristicsDto
import io.graphqlfederation.planetservice.web.dto.CharacteristicsInputDto
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import org.dataloader.DataLoader
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import javax.inject.Singleton

@Singleton
class GetPlanetsFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<List<PlanetDto>> {
    override fun get(env: DataFetchingEnvironment): List<PlanetDto> = planetService.getAll()
        .map { planetConverter.toDto(it) }
}

@Singleton
class GetPlanetFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<PlanetDto> {
    override fun get(env: DataFetchingEnvironment): PlanetDto {
        val id = env.getArgument<String>("id").toLong()
        return planetConverter.toDto(planetService.getById(id))
    }
}

@Singleton
class GetPlanetByNameFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<PlanetDto> {
    override fun get(env: DataFetchingEnvironment): PlanetDto {
        val name = env.getArgument<String>("name")
        return planetConverter.toDto(planetService.getByName(name))
    }
}

@Singleton
class CharacteristicsFetcher : DataFetcher<CompletableFuture<CharacteristicsDto>> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun get(env: DataFetchingEnvironment): CompletableFuture<CharacteristicsDto> {
        val planetDto = env.getSource<PlanetDto>()
        log.info("Resolve `characteristics` field for planet: ${planetDto.name}")

        val dataLoader: DataLoader<Long, CharacteristicsDto> = env.getDataLoader("characteristics")

        return dataLoader.load(planetDto.characteristics.id)
    }
}

@Singleton
class CreatePlanetFetcher(
    private val objectMapper: ObjectMapper,
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<PlanetDto> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun get(env: DataFetchingEnvironment): PlanetDto {
        log.info("Trying to create planet")

        val name = env.getArgument<String>("name")
        val type = env.getArgument<Planet.Type>("type")
        val characteristicsInputDto = objectMapper.convertValue(
            env.getArgument("characteristics"), CharacteristicsInputDto::class.java
        )

        val newPlanet = planetService.create(
            name,
            type,
            characteristicsInputDto.meanRadius,
            characteristicsInputDto.earthsMass,
            characteristicsInputDto.population
        )

        return planetConverter.toDto(newPlanet)
    }
}
