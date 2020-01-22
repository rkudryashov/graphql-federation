package io.micronautgraphqlfederation.planetservice.web.graphql.resolver

import io.micronautgraphqlfederation.planetservice.misc.PlanetConverter
import io.micronautgraphqlfederation.planetservice.service.PlanetService
import javax.inject.Singleton

@Singleton
class PlanetResolver(
    private val planetService: PlanetService,
    private val planetConverter: PlanetConverter
) {

// todo add resolver
}