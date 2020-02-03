package io.graphqlfederation.satelliteservice.web.graphql

import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import io.gqljf.federation.FederatedEntityResolver
import io.gqljf.federation.FederatedSchemaBuilder
import io.gqljf.federation.tracing.FederatedTracingInstrumentation
import io.graphqlfederation.satelliteservice.misc.SatelliteConverter
import io.graphqlfederation.satelliteservice.service.SatelliteService
import io.graphqlfederation.satelliteservice.web.dto.PlanetDto
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val getSatellitesFetcher: GetSatellitesFetcher,
    private val getSatelliteFetcher: GetSatelliteFetcher,
    private val getSatelliteByNameFetcher: GetSatelliteByNameFetcher,
    private val lifeExistsFetcher: LifeExistsFetcher,
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter,
    private val customDataFetcherExceptionHandler: CustomDataFetcherExceptionHandler
) {

    // todo use
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver): GraphQL {
        val schemaInputStream = resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()

        val entityResolvers = listOf(
            FederatedEntityResolver<Long, PlanetDto>("Planet", PlanetDto::class.java) { id ->
                val satellites = satelliteService.getByPlanetId(id)
                PlanetDto(
                    id = id,
                    satellites = satellites.map { satelliteConverter.toDto(it) }
                )
            }
        )

        val transformedGraphQLSchema = FederatedSchemaBuilder()
            .schemaInputStream(schemaInputStream)
            .runtimeWiring(createRuntimeWiring())
            .federatedEntitiesResolvers(entityResolvers)
            .build()

        return GraphQL.newGraphQL(transformedGraphQLSchema)
            .queryExecutionStrategy(AsyncExecutionStrategy(customDataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(customDataFetcherExceptionHandler))
            .instrumentation(FederatedTracingInstrumentation())
            .build()
    }

    private fun createRuntimeWiring(): RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query") { builder ->
            builder
                .dataFetcher("getSatellites", getSatellitesFetcher)
                .dataFetcher("getSatellite", getSatelliteFetcher)
                .dataFetcher("getSatelliteByName", getSatelliteByNameFetcher)
        }
        .type("Satellite") { builder ->
            builder.dataFetcher("lifeExists", lifeExistsFetcher)
        }
        .scalar(ExtendedScalars.Date)
        .build()
}
