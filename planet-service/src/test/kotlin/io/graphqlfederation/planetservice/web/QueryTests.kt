package io.graphqlfederation.planetservice.web

import com.fasterxml.jackson.core.type.TypeReference
import io.micronaut.test.annotation.MicronautTest
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.service.GraphQLClient
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class QueryTests {

    @Inject
    private lateinit var graphQLClient: GraphQLClient

    @Test
    fun testGetPlanets() {
        val query = """
            {
              getPlanets {
                id
                name
                type
                characteristics {
                  meanRadius
                  earthsMass
                  ... on InhabitedPlanetCharacteristics {
                    population
                  }
                }
              }
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(
            query,
            object : TypeReference<List<PlanetDto>>() {})

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
    fun testGetById() {
        val earthId = 3
        val query = """
            {
              getPlanet(id: $earthId) {
                id
                name
                type
                characteristics {
                  meanRadius
                  earthsMass
                  ... on InhabitedPlanetCharacteristics {
                    population
                  }
                }
              }
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(
            query,
            object : TypeReference<PlanetDto>() {})

        assertThat(
            response, allOf(
                hasProperty("id", `is`(3L)),
                hasProperty("name", `is`("Earth")),
                hasProperty("type", `is`(Planet.Type.TERRESTRIAL_PLANET)),
                hasProperty(
                    "characteristics", allOf(
                        hasProperty("meanRadius", `is`(6371.0)),
                        hasProperty("earthsMass", `is`(1.0))
                    )
                )
            )
        )
    }

    @Test
    fun testGetByName() {
        val earthName = "Earth"
        val query = """
            {
              getPlanetByName(name: "$earthName") {
                id
                name
                type
                characteristics {
                  meanRadius
                  earthsMass
                  ... on InhabitedPlanetCharacteristics {
                    population
                  }
                }
              }
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(
            query,
            object : TypeReference<PlanetDto>() {})

        assertThat(
            response, allOf(
                hasProperty("id", `is`(3L)),
                hasProperty("name", `is`("Earth")),
                hasProperty("type", `is`(Planet.Type.TERRESTRIAL_PLANET)),
                hasProperty(
                    "characteristics", allOf(
                        hasProperty("meanRadius", `is`(6371.0)),
                        hasProperty("earthsMass", `is`(1.0))
                    )
                )
            )
        )
    }
}