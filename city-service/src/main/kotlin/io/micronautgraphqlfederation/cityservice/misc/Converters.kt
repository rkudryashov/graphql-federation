package io.micronautgraphqlfederation.cityservice.misc

import io.micronautgraphqlfederation.cityservice.model.City
import io.micronautgraphqlfederation.cityservice.web.dto.CityDto
import javax.inject.Singleton

interface GenericConverter<E, D> {
    fun toDto(entity: E): D
}

@Singleton
class CityConverter : GenericConverter<City, CityDto> {
    override fun toDto(entity: City): CityDto = CityDto(
        id = entity.id,
        name = entity.name
    )
}