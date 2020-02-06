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
    private val getPlanetsFetcher: GetPlanetsFetcher,
    private val getPlanetFetcher: GetPlanetFetcher,
    private val getPlanetByNameFetcher: GetPlanetByNameFetcher,
    private val createPlanetFetcher: CreatePlanetFetcher,
    private val latestPlanetFetcher: LatestPlanetFetcher,
    private val paramsFetcher: ParamsFetcher,
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
            .build()

        return GraphQL.newGraphQL(transformedGraphQLSchema)
            .instrumentation(
                ChainedInstrumentation(
                    listOf(
                        FederatedTracingInstrumentation(),
                        MaxQueryComplexityInstrumentation(60),
                        MaxQueryDepthInstrumentation(15)
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
                    .dataFetcher("getPlanets", getPlanetsFetcher)
                    .dataFetcher("getPlanet", getPlanetFetcher)
                    .dataFetcher("getPlanetByName", getPlanetByNameFetcher)
            }
            .type("Mutation") { builder ->
                builder.dataFetcher("createPlanet", createPlanetFetcher)
            }
            .type("Subscription") { builder ->
                builder.dataFetcher("latestPlanet", latestPlanetFetcher)
            }
            .type("Planet") { builder ->
                builder.dataFetcher("params", paramsFetcher)
            }
            .type("Params") { builder ->
                builder.typeResolver(paramsTypeResolver)
            }
            .type("Type") { builder ->
                builder.enumValues(NaturalEnumValuesProvider(Planet.Type::class.java))
            }
            .build()
    }

    @Bean
    @Singleton
    fun paramsBatchLoader(): BatchLoader<Long, ParamsDto> = BatchLoader { keys ->
        CompletableFuture.supplyAsync {
            paramsService.getByIds(keys)
                .map { paramsConverter.toDto(it) }
        }
    }

    // bean's default scope is `prototype`
    @Bean
    fun dataLoaderRegistry() = DataLoaderRegistry().apply {
        val paramsDataLoader = DataLoader.newDataLoader(paramsBatchLoader())
        register("params", paramsDataLoader)
    }
}
