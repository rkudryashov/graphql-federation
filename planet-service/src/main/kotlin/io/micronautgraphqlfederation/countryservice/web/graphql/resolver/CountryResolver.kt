package io.micronautgraphqlfederation.countryservice.web.graphql.resolver

import io.micronautgraphqlfederation.countryservice.misc.CountryConverter
import io.micronautgraphqlfederation.countryservice.service.CountryService
import javax.inject.Singleton

@Singleton
class CountryResolver(
    private val countryService: CountryService,
    private val countryConverter: CountryConverter
) {

// todo add resolver
}