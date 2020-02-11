package io.graphqlfederation.satelliteservice.web.graphql

import graphql.GraphQL
import graphql.analysis.FieldComplexityCalculator
import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.analysis.MaxQueryDepthInstrumentation
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
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
    private val satellitesDataFetcher: SatellitesDataFetcher,
    private val satelliteDataFetcher: SatelliteDataFetcher,
    private val satelliteByNameDataFetcher: SatelliteByNameDataFetcher,
    private val lifeExistsDataFetcher: LifeExistsDataFetcher,
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter,
    private val customDataFetcherExceptionHandler: CustomDataFetcherExceptionHandler
) {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver): GraphQL {
        val schemaInputStream = resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()

        val log = LoggerFactory.getLogger(FederatedEntityResolver::class.java)

        val planetEntityResolver = object : FederatedEntityResolver<Long, PlanetDto>("Planet", { id ->
            log.info("`Planet` entity with id=$id was requested")
            val satellites = satelliteService.getByPlanetId(id)
            PlanetDto(id = id, satellites = satellites.map { satelliteConverter.toDto(it) })
        }) {}

        val transformedGraphQLSchema = FederatedSchemaBuilder()
            .schemaInputStream(schemaInputStream)
            .runtimeWiring(createRuntimeWiring())
            .federatedEntitiesResolvers(listOf(planetEntityResolver))
            .build()

        return GraphQL.newGraphQL(transformedGraphQLSchema)
            .queryExecutionStrategy(AsyncExecutionStrategy(customDataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(customDataFetcherExceptionHandler))
            .instrumentation(
                ChainedInstrumentation(
                    listOf(
                        FederatedTracingInstrumentation(),
                        MaxQueryComplexityInstrumentation(50, FieldComplexityCalculator { env, child ->
                            1 + child
                        }),
                        MaxQueryDepthInstrumentation(5)
                    )
                )
            )
            .build()
    }

    private fun createRuntimeWiring(): RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query") { builder ->
            builder
                .dataFetcher("satellites", satellitesDataFetcher)
                .dataFetcher("satellite", satelliteDataFetcher)
                .dataFetcher("satelliteByName", satelliteByNameDataFetcher)
        }
        .type("Satellite") { builder ->
            builder.dataFetcher("lifeExists", lifeExistsDataFetcher)
        }
        .scalar(ExtendedScalars.Date)
        .build()
}
