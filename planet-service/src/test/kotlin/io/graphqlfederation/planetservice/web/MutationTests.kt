package io.graphqlfederation.planetservice.web

import com.fasterxml.jackson.core.type.TypeReference
import io.graphqlfederation.planetservice.model.InhabitedPlanetDetails
import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.model.UninhabitedPlanetDetails
import io.graphqlfederation.planetservice.repository.DetailsRepository
import io.graphqlfederation.planetservice.repository.PlanetRepository
import io.graphqlfederation.planetservice.service.GraphQLClient
import io.graphqlfederation.planetservice.web.dto.PlanetDto
import io.micronaut.test.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.core.StringStartsWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import javax.inject.Inject

@MicronautTest
class MutationTests {

    @Inject
    private lateinit var graphQLClient: GraphQLClient
    @Inject
    private lateinit var planetRepository: PlanetRepository
    @Inject
    private lateinit var detailsRepository: DetailsRepository

    private val testingFieldsFragment = """
        fragment testingFields on Planet { 
            id
            name
            type
            details {
                meanRadius
                mass
                ... on InhabitedPlanetDetails {
                    population
                }
            }
        }
    """.trimIndent()

    @Test
    fun testCreateInhabitedPlanet() {
        val planetCount = planetRepository.count()

        val name = "Unexpected inhabited planet"
        val type = Planet.Type.DWARF_PLANET
        val meanRadius = 10.2
        val massNumber = 0.1
        val massTenPower = 25
        val population = 0.0001

        val mutation = """
            mutation {
                createPlanet (name: "$name", type: $type, details: { meanRadius: $meanRadius, mass: { number: $massNumber, tenPower: $massTenPower }, population: $population}) {
                    ... testingFields
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val response = graphQLClient.sendRequest(mutation, object : TypeReference<PlanetDto>() {})

        assertEquals(planetCount + 1, planetRepository.count())

        val createdPlanetId = response.id

        val massMatcher = comparesEqualTo(massNumber.toBigDecimal().multiply(BigDecimal.TEN.pow(massTenPower)))

        assertThat(
            response, allOf(
                hasProperty("id", `is`(createdPlanetId)),
                hasProperty("name", `is`(name)),
                hasProperty("type", `is`(type)),
                hasProperty(
                    "details", allOf(
                        hasProperty("meanRadius", `is`(meanRadius)),
                        hasProperty("mass", massMatcher)
                    )
                )
            )
        )

        val createdPlanet = planetRepository.findById(createdPlanetId).orElseThrow { RuntimeException("Unexpected database state") }
        val createdPlanetDetails = detailsRepository.findById(createdPlanet.detailsId)
        assertTrue(createdPlanetDetails.isPresent)
        assertEquals(InhabitedPlanetDetails::class.java, createdPlanetDetails.get()::class.java)
    }

    @Test
    fun testCreateUninhabitedPlanet() {
        val planetCount = planetRepository.count()

        val name = "Pluto"
        val type = Planet.Type.DWARF_PLANET
        val meanRadius = 10.2
        val massNumber = 0.0146
        val massTenPower = 24

        val mutation = """
            mutation {
                createPlanet (name: "$name", type: $type, details: { meanRadius: $meanRadius, mass: { number: $massNumber, tenPower: $massTenPower }}) {
                    ... testingFields
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val response = graphQLClient.sendRequest(mutation, object : TypeReference<PlanetDto>() {})

        assertEquals(planetCount + 1, planetRepository.count())

        val createdPlanetId = response.id

        val massMatcher = comparesEqualTo(massNumber.toBigDecimal().multiply(BigDecimal.TEN.pow(massTenPower)))

        assertThat(
            response, allOf(
                hasProperty("id", `is`(createdPlanetId)),
                hasProperty("name", `is`(name)),
                hasProperty("type", `is`(type)),
                hasProperty(
                    "details", allOf(
                        hasProperty("meanRadius", `is`(meanRadius)),
                        hasProperty("mass", massMatcher)
                    )
                )
            )
        )

        val createdPlanet = planetRepository.findById(createdPlanetId).orElseThrow { RuntimeException("Unexpected database state") }
        val createdPlanetDetails = detailsRepository.findById(createdPlanet.detailsId)
        assertTrue(createdPlanetDetails.isPresent)
        assertEquals(UninhabitedPlanetDetails::class.java, createdPlanetDetails.get()::class.java)
    }

    @Test
    fun testCreatePlanetWithExistedName() {
        val name = "Neptune"
        val type = Planet.Type.ICE_GIANT
        val meanRadius = 107.5
        val massNumber = 102.0
        val massTenPower = 24

        val mutation = """
            mutation {
                createPlanet (name: "$name", type: $type, details: { meanRadius: $meanRadius, mass: { number: $massNumber, tenPower: $massTenPower }}) {
                    ... testingFields
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val exception = assertThrows<RuntimeException>("Should throw an Exception") {
            graphQLClient.sendRequest(mutation, object : TypeReference<PlanetDto>() {})
        }

        assertThat(
            exception.message,
            StringStartsWith(
                "Exception during execution of GraphQL query/mutation: [Exception while fetching data (/createPlanet) : org.hibernate.exception.ConstraintViolationException: could not execute statement"
            )
        )
    }
}
