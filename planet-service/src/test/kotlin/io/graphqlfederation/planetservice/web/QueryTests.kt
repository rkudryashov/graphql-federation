package io.graphqlfederation.planetservice.web

import com.fasterxml.jackson.core.type.TypeReference
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.GraphQLClient
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import io.micronaut.test.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import javax.inject.Inject

@MicronautTest
class QueryTests {

    @Inject
    private lateinit var graphQLClient: GraphQLClient

    private val testingFieldsFragment = """
        fragment testingFields on Planet { 
            id
            name
            type
            params {
                meanRadius
                mass
                ... on InhabitedPlanetParams {
                    population
                }
            }
        }
    """.trimIndent()

    @Test
    fun testPlanets() {
        val query = """
            {
                planets {
                    id
                    name
                    type
                    params {
                        meanRadius
                        mass
                        ... on InhabitedPlanetParams {
                            population
                        }
                    }
                }
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(query, object : TypeReference<List<PlanetDto>>() {})

        assertThat(response, hasSize(8))
        assertThat(
            response, contains(
                hasProperty("name", `is`("Mercury")),
                hasProperty("name", `is`("Venus")),
                hasProperty("name", `is`("Earth")),
                hasProperty("name", `is`("Mars")),
                hasProperty("name", `is`("Jupiter")),
                hasProperty("name", `is`("Saturn")),
                hasProperty("name", `is`("Uranus")),
                hasProperty("name", `is`("Neptune"))
            )
        )
    }

    @Test
    fun testPlanetById() {
        val earthId = 3
        val query = """
            {
                planet(id: $earthId) {
                    ... testingFields
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val response = graphQLClient.sendRequest(query, object : TypeReference<PlanetDto>() {})

        assertThat(
            response, allOf(
                hasProperty("id", `is`(3L)),
                hasProperty("name", `is`("Earth")),
                hasProperty("type", `is`(Planet.Type.TERRESTRIAL_PLANET)),
                hasProperty(
                    "params", allOf(
                        hasProperty("meanRadius", `is`(6371.0)),
                        hasProperty("mass", comparesEqualTo(5.97.toBigDecimal().multiply(BigDecimal.TEN.pow(24))))
                    )
                )
            )
        )
    }

    @Test
    fun testPlanetByName() {
        val variables = mapOf("name" to "Earth")
        val query = """
            query testPlanetByName(${'$'}name: String!){
                planetByName(name: ${'$'}name) {
                    ... testingFields
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val response = graphQLClient.sendRequest(query, variables, null, object : TypeReference<PlanetDto>() {})

        assertThat(
            response, allOf(
                hasProperty("id", `is`(3L)),
                hasProperty("name", `is`("Earth")),
                hasProperty("type", `is`(Planet.Type.TERRESTRIAL_PLANET)),
                hasProperty(
                    "params", allOf(
                        hasProperty("meanRadius", `is`(6371.0)),
                        hasProperty("mass", comparesEqualTo(5.97.toBigDecimal().multiply(BigDecimal.TEN.pow(24))))
                    )
                )
            )
        )
    }
}
