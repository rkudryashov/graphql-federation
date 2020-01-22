package io.micronautgraphqlfederation.satelliteservice.web.graphql

import com.apollographql.federation.graphqljava.Federation
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import java.io.InputStreamReader
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val satellitesFetcher: SatellitesFetcher,
    private val satelliteFetcher: SatelliteFetcher
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

    private fun createRuntimeWiring(): RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("satellites", satellitesFetcher)
                .dataFetcher("satellite", satelliteFetcher)
        )
        .build()
}
