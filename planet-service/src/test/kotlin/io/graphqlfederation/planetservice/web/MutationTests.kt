package io.graphqlfederation.planetservice.web

import com.fasterxml.jackson.core.type.TypeReference
import io.micronaut.test.annotation.MicronautTest
import io.graphqlfederation.planetservice.model.InhabitedPlanetCharacteristics
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.model.UninhabitedPlanetCharacteristics
import io.graphqlfederation.planetservice.repository.CharacteristicsRepository
import io.graphqlfederation.planetservice.repository.PlanetRepository
import io.graphqlfederation.planetservice.service.GraphQLClient
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.core.StringStartsWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject

@MicronautTest
class MutationTests {

    @Inject
    private lateinit var graphQLClient: GraphQLClient
    @Inject
    private lateinit var planetRepository: PlanetRepository
    @Inject
    private lateinit var characteristicsRepository: CharacteristicsRepository

    @Test
    fun testCreateInhabitedPlanet() {
        val planetCount = planetRepository.count()

        val name = "Unexpected inhabited planet"
        val type = Planet.Type.DWARF_PLANET
        val meanRadius = 10.2
        val earthsMass = 0.03
        val population = 0.0001

        val query = """
              mutation {
                createPlanet (name: "$name", type: $type, characteristics: { meanRadius: $meanRadius, earthsMass: $earthsMass, population: $population}) {
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

        assertEquals(planetCount + 1, planetRepository.count())

        val createdPlanetId = response.id

        assertThat(
            response, allOf(
                hasProperty("id", `is`(createdPlanetId)),
                hasProperty("name", `is`(name)),
                hasProperty("type", `is`(type)),
                hasProperty(
                    "characteristics", allOf(
                        hasProperty("meanRadius", `is`(meanRadius)),
                        hasProperty("earthsMass", `is`(earthsMass))
                    )
                )
            )
        )

        val createdPlanet =
            planetRepository.findById(createdPlanetId).orElseThrow { RuntimeException("Unexpected database state") }
        val createdPlanetCharacteristics = characteristicsRepository.findById(createdPlanet.characteristicsId)
        assertTrue(createdPlanetCharacteristics.isPresent)
        assertEquals(InhabitedPlanetCharacteristics::class.java, createdPlanetCharacteristics.get()::class.java)
    }

    @Test
    fun testCreateUninhabitedPlanet() {
        val planetCount = planetRepository.count()

        val name = "Unexpected uninhabited planet"
        val type = Planet.Type.DWARF_PLANET
        val meanRadius = 10.2
        val earthsMass = 0.03

        val query = """
              mutation {
                createPlanet (name: "$name", type: $type, characteristics: { meanRadius: $meanRadius, earthsMass: $earthsMass }) {
                    id
                    name
                    type
                    characteristics {
                        meanRadius
                        earthsMass
                    }
                }
              }
        """.trimIndent()

        val response = graphQLClient.sendRequest(
            query,
            object : TypeReference<PlanetDto>() {})

        assertEquals(planetCount + 1, planetRepository.count())

        val createdPlanetId = response.id

        assertThat(
            response, allOf(
                hasProperty("id", `is`(createdPlanetId)),
                hasProperty("name", `is`(name)),
                hasProperty("type", `is`(type)),
                hasProperty(
                    "characteristics", allOf(
                        hasProperty("meanRadius", `is`(meanRadius)),
                        hasProperty("earthsMass", `is`(earthsMass))
                    )
                )
            )
        )

        val createdPlanet =
            planetRepository.findById(createdPlanetId).orElseThrow { RuntimeException("Unexpected database state") }
        val createdPlanetCharacteristics = characteristicsRepository.findById(createdPlanet.characteristicsId)
        assertTrue(createdPlanetCharacteristics.isPresent)
        assertEquals(UninhabitedPlanetCharacteristics::class.java, createdPlanetCharacteristics.get()::class.java)
    }

    @Test
    fun testCreatePlanetWithExistedName() {
        val name = "Neptune"
        val type = Planet.Type.ICE_GIANT
        val meanRadius = 107.5
        val earthsMass = 15.1

        val query = """
              mutation {
                createPlanet (name: "$name", type: $type, characteristics: { meanRadius: $meanRadius, earthsMass: $earthsMass }) {
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

        val exception = assertThrows<RuntimeException>("Should throw an Exception") {
            graphQLClient.sendRequest(
                query,
                object : TypeReference<PlanetDto>() {})
        }

        assertThat(
            exception.message,
            StringStartsWith(
                "Exception during execution of GraphQL query/mutation: [Exception while fetching data (/createPlanet) : org.hibernate.exception.ConstraintViolationException: could not execute statement"
            )
        )
    }
}