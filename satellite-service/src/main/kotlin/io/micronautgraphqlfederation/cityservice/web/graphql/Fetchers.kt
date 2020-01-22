package io.micronautgraphqlfederation.cityservice.web.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.micronautgraphqlfederation.cityservice.misc.CityConverter
import io.micronautgraphqlfederation.cityservice.service.CityService
import io.micronautgraphqlfederation.cityservice.web.dto.CityDto
import javax.inject.Singleton

@Singleton
class CitiesFetcher(
    private val cityService: CityService,
    private val cityConverter: CityConverter
) : DataFetcher<List<CityDto>> {
    override fun get(env: DataFetchingEnvironment): List<CityDto> = cityService.getAll()
        .map { cityConverter.toDto(it) }
}

@Singleton
class CityFetcher(
    private val cityService: CityService,
    private val cityConverter: CityConverter
) : DataFetcher<CityDto> {
    override fun get(env: DataFetchingEnvironment): CityDto {
        val id: Long = env.getArgument<String>("id").toLong()
        return cityConverter.toDto(cityService.getById(id))
    }
}

// todo fetcher by name