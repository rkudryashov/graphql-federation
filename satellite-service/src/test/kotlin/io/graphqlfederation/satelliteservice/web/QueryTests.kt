package io.graphqlfederation.satelliteservice.web

import com.fasterxml.jackson.core.type.TypeReference
import io.graphqlfederation.satelliteservice.service.GraphQLClient
import io.micronaut.test.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@MicronautTest
class QueryTests {

    @Inject
    private lateinit var graphQLClient: GraphQLClient

    private val testingFieldsFragment = """
        fragment testingFields on Satellite {
            id
            name
            firstSpacecraftLandingDate
        }
    """.trimIndent()

    @Test
    fun testSatellites() {
        val query = """
            {
                satellites {
                    ... testingFields
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val response = graphQLClient.sendRequest(query, object : TypeReference<List<TestSatelliteDto>>() {})

        assertThat(response, hasSize(14))
        assertThat(
            response, hasItems(
                allOf(
                    hasProperty("name", `is`("Moon")),
                    hasProperty(
                        "firstSpacecraftLandingDate",
                        `is`(LocalDate.of(1959, Month.SEPTEMBER, 13))
                    )
                ),
                allOf(
                    hasProperty("name", `is`("Titan")),
                    hasProperty("firstSpacecraftLandingDate", nullValue())
                )
            )
        )
    }

    @Test
    fun testSatelliteById() {
        val moonId = 1
        val query = """
            {
                satellite(id: $moonId) {
                    ... testingFields
                }
            }

            $testingFieldsFragment            
        """.trimIndent()

        val response = graphQLClient.sendRequest(query, object : TypeReference<TestSatelliteDto>() {})

        assertThat(
            response, allOf(
                hasProperty("id", `is`(1L)),
                hasProperty("name", `is`("Moon")),
                hasProperty(
                    "firstSpacecraftLandingDate",
                    `is`(LocalDate.of(1959, Month.SEPTEMBER, 13))
                )
            )
        )
    }

    @Test
    fun testSatelliteByName() {
        val titanName = "Titan"
        val query = """
            {
                satelliteByName(name: "$titanName") {
                    ... testingFields
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val response = graphQLClient.sendRequest(query, object : TypeReference<TestSatelliteDto>() {})

        assertThat(
            response, allOf(
                hasProperty("id", `is`(8L)),
                hasProperty("name", `is`("Titan")),
                hasProperty("firstSpacecraftLandingDate", nullValue())
            )
        )
    }

    @Test
    fun testSatelliteByNameShouldThrowException() {
        val titanName = "Titan"
        val query = """
            {
                satelliteByName(name: "$titanName") {
                    ... testingFields
                    lifeExists
                }
            }

            $testingFieldsFragment
        """.trimIndent()

        val exception = assertThrows<RuntimeException> {
            graphQLClient.sendRequest(query, object : TypeReference<TestSatelliteDto>() {})
        }

        assertThat(
            exception.message,
            `is`("Exception during execution of GraphQL query/mutation: [There was an error: `lifeExists` property can only be accessed by authenticated users\n, Cannot return null for non-nullable type: 'LifeExists' within parent 'Satellite' (/satelliteByName/lifeExists)\n]")
        )
    }
}
