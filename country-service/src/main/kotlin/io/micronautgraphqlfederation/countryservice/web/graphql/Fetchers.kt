package io.micronautgraphqlfederation.countryservice.web.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.micronautgraphqlfederation.countryservice.misc.CountryConverter
import io.micronautgraphqlfederation.countryservice.service.CountryService
import io.micronautgraphqlfederation.countryservice.web.dto.CountryDto
import javax.inject.Singleton

@Singleton
class CountriesFetcher(
    private val countryService: CountryService,
    private val countryConverter: CountryConverter
) : DataFetcher<List<CountryDto>> {
    override fun get(env: DataFetchingEnvironment): List<CountryDto> = countryService.getAll()
        .map { countryConverter.toDto(it) }
}

@Singleton
class CountryFetcher(
    private val countryService: CountryService,
    private val countryConverter: CountryConverter
) : DataFetcher<CountryDto> {
    override fun get(env: DataFetchingEnvironment): CountryDto {
        val id: Long = env.getArgument<String>("id").toLong()
        return countryConverter.toDto(countryService.getById(id))
    }
}

// todo fetcher by name