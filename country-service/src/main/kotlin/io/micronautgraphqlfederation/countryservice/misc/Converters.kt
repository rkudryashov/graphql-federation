package io.micronautgraphqlfederation.countryservice.misc

import io.micronautgraphqlfederation.countryservice.model.Country
import io.micronautgraphqlfederation.countryservice.web.dto.CountryDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class CountryConverter : GenericConverter<Country, CountryDto> {
    override fun toDto(entity: Country): CountryDto =
        CountryDto(
            id = entity.id,
            name = entity.name
        )
}