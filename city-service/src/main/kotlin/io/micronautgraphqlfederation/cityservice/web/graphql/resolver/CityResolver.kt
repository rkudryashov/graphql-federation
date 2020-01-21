package io.micronautgraphqlfederation.cityservice.web.graphql.resolver

import io.micronautgraphqlfederation.cityservice.misc.CityConverter
import io.micronautgraphqlfederation.cityservice.service.CityService
import javax.inject.Singleton

@Singleton
class CityResolver(
    private val cityService: CityService,
    private val cityConverter: CityConverter
) {

// todo add resolver
}