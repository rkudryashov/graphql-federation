package io.micronautgraphqlfederation.planetservice.web.graphql

import com.apollographql.federation.graphqljava.Federation
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring.newTypeWiring
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import io.micronautgraphqlfederation.planetservice.web.graphql.resolver.PlanetResolver
import java.io.InputStreamReader
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val planetsFetcher: PlanetsFetcher,
    private val planetFetcher: PlanetFetcher,
    private val planetResolver: PlanetResolver
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
            newTypeWiring("Query")
                .dataFetcher("planets", planetsFetcher)
                .dataFetcher("planet", planetFetcher)

        )
        .type(
            newTypeWiring("Planet")
                .dataFetcher("characteristics") { env -> planetResolver.characteristics(env.getSource()) }
        )
        .build()
}
