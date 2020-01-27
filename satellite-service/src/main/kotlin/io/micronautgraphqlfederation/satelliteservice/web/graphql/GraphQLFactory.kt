package io.micronautgraphqlfederation.satelliteservice.web.graphql

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava._Entity
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.TypeResolver
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.TypeRuntimeWiring
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import io.micronautgraphqlfederation.satelliteservice.misc.SatelliteConverter
import io.micronautgraphqlfederation.satelliteservice.service.SatelliteService
import io.micronautgraphqlfederation.satelliteservice.web.dto.PlanetDto
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val getSatellitesFetcher: GetSatellitesFetcher,
    private val getSatelliteFetcher: GetSatelliteFetcher,
    private val satelliteService: SatelliteService,
    private val satelliteConverter: SatelliteConverter
) {

    // todo use
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver): GraphQL {
        val schemaResource = resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()

        val entitiesDataFetcher = DataFetcher { env ->
            val planetsData = env.getArgument<List<Map<String, Any>>>(_Entity.argumentName)
            planetsData.map { values ->
                val id = (values["id"] as String).toLong()
                val satellites = satelliteService.getByPlanetId(id)
                PlanetDto(
                    id = id,
                    satellites = satellites.map { satelliteConverter.toDto(it) }
                )
            }
        }

        val planetTypeResolver = TypeResolver { env ->
            env.schema.getObjectType("Planet")
        }

        val transformedGraphQLSchema = Federation.transform(InputStreamReader(schemaResource), createRuntimeWiring())
            .fetchEntities(entitiesDataFetcher)
            .resolveEntityType(planetTypeResolver)
            .build()

        return GraphQL.newGraphQL(transformedGraphQLSchema)
            // todo how to use it?
            .instrumentation(FederatedTracingInstrumentation())
            .build()
    }

    private fun createRuntimeWiring(): RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("getSatellites", getSatellitesFetcher)
                .dataFetcher("getSatellite", getSatelliteFetcher)
        )
        .build()
}
