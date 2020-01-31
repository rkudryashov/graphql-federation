package io.graphqlfederation.planetservice.web.graphql

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import graphql.GraphQL
import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.analysis.MaxQueryDepthInstrumentation
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.schema.TypeResolver
import graphql.schema.idl.NaturalEnumValuesProvider
import graphql.schema.idl.RuntimeWiring
import io.graphqlfederation.planetservice.misc.CharacteristicsConverter
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.CharacteristicsService
import io.graphqlfederation.planetservice.web.dto.CharacteristicsDto
import io.graphqlfederation.planetservice.web.dto.InhabitedPlanetCharacteristicsDto
import io.graphqlfederation.planetservice.web.dto.UninhabitedPlanetCharacteristicsDto
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val getPlanetsFetcher: GetPlanetsFetcher,
    private val getPlanetFetcher: GetPlanetFetcher,
    private val getPlanetByNameFetcher: GetPlanetByNameFetcher,
    private val createPlanetFetcher: CreatePlanetFetcher,
    private val characteristicsFetcher: CharacteristicsFetcher,
    private val characteristicsService: CharacteristicsService,
    private val characteristicsConverter: CharacteristicsConverter
) {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver): GraphQL {
        val schemaResource = resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()

        val planetTypeResolver = TypeResolver { env ->
            env.schema.getObjectType("Planet")
        }

        val transformedGraphQLSchema = Federation.transform(InputStreamReader(schemaResource), createRuntimeWiring())
            .fetchEntities {}
            .resolveEntityType(planetTypeResolver)
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
        val characteristicsTypeResolver = TypeResolver { env ->
            when (val characteristics = env.getObject() as CharacteristicsDto) {
                is InhabitedPlanetCharacteristicsDto -> env.schema.getObjectType("InhabitedPlanetCharacteristics")
                is UninhabitedPlanetCharacteristicsDto -> env.schema.getObjectType("UninhabitedPlanetCharacteristics")
                else -> throw RuntimeException("Unexpected characteristics type: ${characteristics.javaClass.name}")
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
            .type("Planet") { builder ->
                builder.dataFetcher("characteristics", characteristicsFetcher)
            }
            .type("Characteristics") { builder ->
                builder.typeResolver(characteristicsTypeResolver)
            }
            .type("Type") { builder ->
                builder.enumValues(NaturalEnumValuesProvider(Planet.Type::class.java))
            }
            .build()
    }

    @Bean
    @Singleton
    fun characteristicsBatchLoader(): BatchLoader<Long, CharacteristicsDto> = BatchLoader { keys ->
        CompletableFuture.supplyAsync {
            characteristicsService.getByIds(keys)
                .map { characteristicsConverter.toDto(it) }
        }
    }

    // bean's default scope is `prototype`
    @Bean
    fun characteristicsDataLoader(): DataLoader<Long, CharacteristicsDto> =
        DataLoader.newDataLoader(characteristicsBatchLoader())

    // bean's default scope is `prototype`
    @Bean
    fun dataLoaderRegistry(): DataLoaderRegistry = DataLoaderRegistry().apply {
        register("characteristics", characteristicsDataLoader())
    }
}
