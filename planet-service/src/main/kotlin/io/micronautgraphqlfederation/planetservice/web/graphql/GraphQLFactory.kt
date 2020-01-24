package io.micronautgraphqlfederation.planetservice.web.graphql

import com.apollographql.federation.graphqljava.Federation
import graphql.GraphQL
import graphql.schema.TypeResolver
import graphql.schema.idl.NaturalEnumValuesProvider
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import io.micronautgraphqlfederation.planetservice.misc.CharacteristicsConverter
import io.micronautgraphqlfederation.planetservice.model.Planet
import io.micronautgraphqlfederation.planetservice.service.CharacteristicsService
import io.micronautgraphqlfederation.planetservice.web.dto.CharacteristicsDto
import io.micronautgraphqlfederation.planetservice.web.dto.InhabitedPlanetDto
import io.micronautgraphqlfederation.planetservice.web.dto.PlanetDto
import io.micronautgraphqlfederation.planetservice.web.dto.UninhabitedPlanetDto
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val planetsFetcher: PlanetsFetcher,
    private val planetFetcher: PlanetFetcher,
    private val createPlanetFetcher: CreatePlanetFetcher,
    private val characteristicsFetcher: CharacteristicsFetcher,
    private val characteristicsService: CharacteristicsService,
    private val characteristicsConverter: CharacteristicsConverter
) {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver): GraphQL {
        val schemaResource = resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()
        val typeDefinitionRegistry = SchemaParser().parse(InputStreamReader(schemaResource))
        val graphQLSchema = SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, createRuntimeWiring())
        val transformedGraphQLSchema = Federation.transform(graphQLSchema).build()

        return GraphQL.newGraphQL(transformedGraphQLSchema).build()
    }

    // todo how to create dataFetcher for interface?
    private fun createRuntimeWiring(): RuntimeWiring {
        val planetResolver = TypeResolver { env ->
            when (env.getObject() as PlanetDto) {
                is InhabitedPlanetDto -> env.schema.getObjectType("InhabitedPlanet")
                is UninhabitedPlanetDto -> env.schema.getObjectType("UninhabitedPlanet")
            }
        }

        return RuntimeWiring.newRuntimeWiring()
            .type("Query") { builder ->
                builder
                    .dataFetcher("planets", planetsFetcher)
                    .dataFetcher("planet", planetFetcher)
            }
            .type("Mutation") { builder ->
                builder.dataFetcher("createPlanet", createPlanetFetcher)
            }
            .type("Planet") { builder ->
                builder.typeResolver(planetResolver)
            }
            .type("InhabitedPlanet") { builder ->
                builder.dataFetcher("characteristics", characteristicsFetcher)
            }
            .type("UninhabitedPlanet") { builder ->
                builder.dataFetcher("characteristics", characteristicsFetcher)
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
