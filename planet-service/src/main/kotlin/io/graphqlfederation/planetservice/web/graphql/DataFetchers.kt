package io.graphqlfederation.planetservice.web.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.graphqlfederation.planetservice.misc.PlanetConverter
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.PlanetService
import io.graphqlfederation.planetservice.web.dto.DetailsDto
import io.graphqlfederation.planetservice.web.dto.DetailsInputDto
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import org.dataloader.DataLoader
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import javax.inject.Singleton

@Singleton
class PlanetsDataFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<List<PlanetDto>> {
    override fun get(env: DataFetchingEnvironment): List<PlanetDto> = planetService.getAll()
        .map { planetConverter.toDto(it) }
}

@Singleton
class PlanetDataFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<PlanetDto> {
    override fun get(env: DataFetchingEnvironment): PlanetDto {
        val id = env.getArgument<String>("id").toLong()
        return planetConverter.toDto(planetService.getById(id))
    }
}

@Singleton
class PlanetByNameDataFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<PlanetDto> {
    override fun get(env: DataFetchingEnvironment): PlanetDto {
        val name = env.getArgument<String>("name")
        return planetConverter.toDto(planetService.getByName(name))
    }
}

@Singleton
class CreatePlanetDataFetcher(
    private val objectMapper: ObjectMapper,
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<PlanetDto> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun get(env: DataFetchingEnvironment): PlanetDto {
        log.info("Trying to create planet")

        val name = env.getArgument<String>("name")
        val type = env.getArgument<Planet.Type>("type")
        val detailsInputDto = objectMapper.convertValue(env.getArgument("details"), DetailsInputDto::class.java)

        val newPlanet = planetService.create(
            name,
            type,
            detailsInputDto.meanRadius,
            detailsInputDto.mass.number,
            detailsInputDto.mass.tenPower,
            detailsInputDto.population
        )

        return planetConverter.toDto(newPlanet)
    }
}

@Singleton
class LatestPlanetDataFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<Publisher<PlanetDto>> {

    override fun get(environment: DataFetchingEnvironment) = planetService.getLatestPlanet().map { planetConverter.toDto(it) }
}

@Singleton
class DetailsDataFetcher : DataFetcher<CompletableFuture<DetailsDto>> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun get(env: DataFetchingEnvironment): CompletableFuture<DetailsDto> {
        val planetDto = env.getSource<PlanetDto>()
        log.info("Resolve `details` field for planet: ${planetDto.name}")

        val dataLoader: DataLoader<Long, DetailsDto> = env.getDataLoader("details")

        return dataLoader.load(planetDto.details.id)
    }
}
