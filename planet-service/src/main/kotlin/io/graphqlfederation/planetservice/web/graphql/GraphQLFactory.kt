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
import io.graphqlfederation.planetservice.misc.ParamsConverter
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.ParamsService
import io.graphqlfederation.planetservice.web.dto.InhabitedPlanetParamsDto
import io.graphqlfederation.planetservice.web.dto.ParamsDto
import io.graphqlfederation.planetservice.web.dto.UninhabitedPlanetParamsDto
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
    private val getPlanetsDataFetcher: GetPlanetsDataFetcher,
    private val getPlanetDataFetcher: GetPlanetDataFetcher,
    private val getPlanetByNameDataFetcher: GetPlanetByNameDataFetcher,
    private val createPlanetDataFetcher: CreatePlanetDataFetcher,
    private val latestPlanetDataFetcher: LatestPlanetDataFetcher,
    private val paramsDataFetcher: ParamsDataFetcher,
    private val paramsService: ParamsService,
    private val paramsConverter: ParamsConverter
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
                        FederatedTracingInstrumentation(),
                        MaxQueryComplexityInstrumentation(50),
                        MaxQueryDepthInstrumentation(5)
                    )
                )
            )
            .build()
    }

    private fun createRuntimeWiring(): RuntimeWiring {
        val paramsTypeResolver = TypeResolver { env ->
            when (val params = env.getObject() as ParamsDto) {
                is InhabitedPlanetParamsDto -> env.schema.getObjectType("InhabitedPlanetParams")
                is UninhabitedPlanetParamsDto -> env.schema.getObjectType("UninhabitedPlanetParams")
                else -> throw RuntimeException("Unexpected params type: ${params.javaClass.name}")
            }
        }

        return RuntimeWiring.newRuntimeWiring()
            .type("Query") { builder ->
                builder
                    .dataFetcher("getPlanets", getPlanetsDataFetcher)
                    .dataFetcher("getPlanet", getPlanetDataFetcher)
                    .dataFetcher("getPlanetByName", getPlanetByNameDataFetcher)
            }
            .type("Mutation") { builder ->
                builder.dataFetcher("createPlanet", createPlanetDataFetcher)
            }
            .type("Subscription") { builder ->
                builder.dataFetcher("latestPlanet", latestPlanetDataFetcher)
            }
            .type("Planet") { builder ->
                builder.dataFetcher("params", paramsDataFetcher)
            }
            .type("Params") { builder ->
                builder.typeResolver(paramsTypeResolver)
            }
            .type("Type") { builder ->
                builder.enumValues(NaturalEnumValuesProvider(Planet.Type::class.java))
            }
            .build()
    }

    // bean's scope is `Singleton`, because `BatchLoader` is stateless
    @Bean
    @Singleton
    fun paramsBatchLoader(): BatchLoader<Long, ParamsDto> = BatchLoader { keys ->
        CompletableFuture.supplyAsync {
            paramsService.getByIds(keys)
                .map { paramsConverter.toDto(it) }
        }
    }

    // bean's (default) scope is `Prototype`, because `DataLoader` is stateful
    @Bean
    fun dataLoaderRegistry() = DataLoaderRegistry().apply {
        val paramsDataLoader = DataLoader.newDataLoader(paramsBatchLoader())
        register("params", paramsDataLoader)
    }
}
