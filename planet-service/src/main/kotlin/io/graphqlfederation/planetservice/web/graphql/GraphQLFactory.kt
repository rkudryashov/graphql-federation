package io.graphqlfederation.planetservice.web.graphql

import graphql.GraphQL
import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.analysis.MaxQueryDepthInstrumentation
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.schema.TypeResolver
import graphql.schema.idl.NaturalEnumValuesProvider
import graphql.schema.idl.RuntimeWiring
import io.gqljf.federation.FederatedSchemaBuilder
import io.gqljf.federation.tracing.FederatedTracingInstrumentation
import io.graphqlfederation.planetservice.misc.DetailsConverter
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.DetailsService
import io.graphqlfederation.planetservice.web.dto.InhabitedPlanetDetailsDto
import io.graphqlfederation.planetservice.web.dto.DetailsDto
import io.graphqlfederation.planetservice.web.dto.UninhabitedPlanetDetailsDto
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.util.concurrent.CompletableFuture
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val planetsDataFetcher: PlanetsDataFetcher,
    private val planetDataFetcher: PlanetDataFetcher,
    private val planetByNameDataFetcher: PlanetByNameDataFetcher,
    private val createPlanetDataFetcher: CreatePlanetDataFetcher,
    private val latestPlanetDataFetcher: LatestPlanetDataFetcher,
    private val detailsDataFetcher: DetailsDataFetcher,
    private val detailsService: DetailsService,
    private val detailsConverter: DetailsConverter
) {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver): GraphQL {
        val schemaInputStream = resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()
        val transformedGraphQLSchema = FederatedSchemaBuilder()
            .schemaInputStream(schemaInputStream)
            .runtimeWiring(createRuntimeWiring())
            .excludeSubscriptionsFromApolloSdl(true)
            .build()

        return GraphQL.newGraphQL(transformedGraphQLSchema)
            .instrumentation(
                ChainedInstrumentation(
                    listOf(
                        FederatedTracingInstrumentation()
                        // uncomment if you need to enable the instrumentations. but this may affect showing documentation in a GraphQL client
                        // MaxQueryComplexityInstrumentation(50),
                        // MaxQueryDepthInstrumentation(5)
                    )
                )
            )
            .build()
    }

    private fun createRuntimeWiring(): RuntimeWiring {
        val detailsTypeResolver = TypeResolver { env ->
            when (val details = env.getObject() as DetailsDto) {
                is InhabitedPlanetDetailsDto -> env.schema.getObjectType("InhabitedPlanetDetails")
                is UninhabitedPlanetDetailsDto -> env.schema.getObjectType("UninhabitedPlanetDetails")
                else -> throw RuntimeException("Unexpected details type: ${details.javaClass.name}")
            }
        }

        return RuntimeWiring.newRuntimeWiring()
            .type("Query") { builder ->
                builder
                    .dataFetcher("planets", planetsDataFetcher)
                    .dataFetcher("planet", planetDataFetcher)
                    .dataFetcher("planetByName", planetByNameDataFetcher)
            }
            .type("Mutation") { builder ->
                builder.dataFetcher("createPlanet", createPlanetDataFetcher)
            }
            .type("Subscription") { builder ->
                builder.dataFetcher("latestPlanet", latestPlanetDataFetcher)
            }
            .type("Planet") { builder ->
                builder.dataFetcher("details", detailsDataFetcher)
            }
            .type("Details") { builder ->
                builder.typeResolver(detailsTypeResolver)
            }
            .type("Type") { builder ->
                builder.enumValues(NaturalEnumValuesProvider(Planet.Type::class.java))
            }
            .build()
    }

    // bean's scope is `Singleton`, because `BatchLoader` is stateless
    @Bean
    @Singleton
    fun detailsBatchLoader(): BatchLoader<Long, DetailsDto> = BatchLoader { keys ->
        CompletableFuture.supplyAsync {
            detailsService.getByIds(keys)
                .map { detailsConverter.toDto(it) }
        }
    }

    // bean's (default) scope is `Prototype`, because `DataLoader` is stateful
    @Bean
    fun dataLoaderRegistry() = DataLoaderRegistry().apply {
        val detailsDataLoader = DataLoader.newDataLoader(detailsBatchLoader())
        register("details", detailsDataLoader)
    }
}
