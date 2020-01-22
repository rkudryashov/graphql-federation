package io.micronautgraphqlfederation.satelliteservice.web.graphql.resolver

import io.micronautgraphqlfederation.satelliteservice.misc.SatelliteConverter
import io.micronautgraphqlfederation.satelliteservice.service.SatelliteService
import javax.inject.Singleton

@Singleton
class SatelliteResolver(
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter
) {

// todo add resolver
}