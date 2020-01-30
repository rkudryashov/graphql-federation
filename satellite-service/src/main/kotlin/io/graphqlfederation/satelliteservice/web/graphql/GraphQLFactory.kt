package io.graphqlfederation.satelliteservice.web.graphql

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava._Entity
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import graphql.GraphQL
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.schema.DataFetcher
import graphql.schema.TypeResolver
import graphql.schema.idl.RuntimeWiring
import io.graphqlfederation.satelliteservice.misc.SatelliteConverter
import io.graphqlfederation.satelliteservice.service.SatelliteService
import io.graphqlfederation.satelliteservice.web.dto.PlanetDto
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val getSatellitesFetcher: GetSatellitesFetcher,
    private val getSatelliteFetcher: GetSatelliteFetcher,
    private val lifeExistsFetcher: LifeExistsFetcher,
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
            val representations = env.getArgument<List<Map<String, Any>>>(_Entity.argumentName)

            representations.map { values ->
                if ("Planet" == values["__typename"]) {
                    val id = (values["id"] as String).toLong()
                    val satellites = satelliteService.getByPlanetId(id)
                    PlanetDto(
                        id = id,
                        satellites = satellites.map { satelliteConverter.toDto(it) }
                    )
                }
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
            .instrumentation(
                ChainedInstrumentation(
                    listOf(
                        FederatedTracingInstrumentation()
                    )
                )
            )
            .build()
    }

    private fun createRuntimeWiring(): RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query") { builder ->
            builder
                .dataFetcher("getSatellites", getSatellitesFetcher)
                .dataFetcher("getSatellite", getSatelliteFetcher)
        }
        .type("Satellite") { builder ->
            builder
                .dataFetcher("lifeExists", lifeExistsFetcher)
        }
        .build()
}
