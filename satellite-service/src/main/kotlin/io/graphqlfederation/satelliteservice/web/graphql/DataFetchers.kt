package io.graphqlfederation.satelliteservice.web.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.graphqlfederation.satelliteservice.misc.SatelliteConverter
import io.graphqlfederation.satelliteservice.model.Satellite
import io.graphqlfederation.satelliteservice.service.SatelliteService
import io.graphqlfederation.satelliteservice.web.dto.SatelliteDto
import javax.inject.Singleton

@Singleton
class SatellitesDataFetcher(
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter
) : DataFetcher<List<SatelliteDto>> {
    override fun get(env: DataFetchingEnvironment): List<SatelliteDto> = satelliteService.getAll()
        .map { satelliteConverter.toDto(it) }
}

@Singleton
class SatelliteDataFetcher(
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter
) : DataFetcher<SatelliteDto> {
    override fun get(env: DataFetchingEnvironment): SatelliteDto? {
        val id = env.getArgument<String>("id").toLong()
        return satelliteService.getById(id)?.let { satelliteConverter.toDto(it) }
    }
}

@Singleton
class SatelliteByNameDataFetcher(
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter
) : DataFetcher<SatelliteDto> {
    override fun get(env: DataFetchingEnvironment): SatelliteDto? {
        val name = env.getArgument<String>("name")
        return satelliteService.getByName(name)?.let { satelliteConverter.toDto(it) }
    }
}

@Singleton
class LifeExistsDataFetcher(
    private val satelliteService: SatelliteService
) : DataFetcher<Satellite.LifeExists> {
    override fun get(env: DataFetchingEnvironment): Satellite.LifeExists {
        val id = env.getSource<SatelliteDto>().id
        return satelliteService.getLifeExists(id)
    }
}
