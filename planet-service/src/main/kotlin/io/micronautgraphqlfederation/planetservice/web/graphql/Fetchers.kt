package io.micronautgraphqlfederation.planetservice.web.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.micronautgraphqlfederation.planetservice.misc.PlanetConverter
import io.micronautgraphqlfederation.planetservice.service.PlanetService
import io.micronautgraphqlfederation.planetservice.web.dto.PlanetDto
import javax.inject.Singleton

@Singleton
class PlanetsFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<List<PlanetDto>> {
    override fun get(env: DataFetchingEnvironment): List<PlanetDto> = planetService.getAll()
        .map { planetConverter.toDto(it) }
}

@Singleton
class PlanetFetcher(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) : DataFetcher<PlanetDto> {
    override fun get(env: DataFetchingEnvironment): PlanetDto {
        val id: Long = env.getArgument<String>("id").toLong()
        return planetConverter.toDto(planetService.getById(id))
    }
}

// todo fetcher by name