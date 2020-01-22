package io.micronautgraphqlfederation.satelliteservice.web.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.micronautgraphqlfederation.satelliteservice.misc.SatelliteConverter
import io.micronautgraphqlfederation.satelliteservice.service.SatelliteService
import io.micronautgraphqlfederation.satelliteservice.web.dto.SatelliteDto
import javax.inject.Singleton

@Singleton
class SatellitesFetcher(
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter
) : DataFetcher<List<SatelliteDto>> {
    override fun get(env: DataFetchingEnvironment): List<SatelliteDto> = satelliteService.getAll()
        .map { satelliteConverter.toDto(it) }
}

@Singleton
class SatelliteFetcher(
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter
) : DataFetcher<SatelliteDto> {
    override fun get(env: DataFetchingEnvironment): SatelliteDto {
        val id: Long = env.getArgument<String>("id").toLong()
        return satelliteConverter.toDto(satelliteService.getById(id))
    }
}

// todo fetcher by name