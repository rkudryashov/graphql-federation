package io.graphqlfederation.satelliteservice.web

import com.fasterxml.jackson.core.type.TypeReference
import io.graphqlfederation.satelliteservice.service.GraphQLClient
import io.micronaut.test.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class QueryTests {

    @Inject
    private lateinit var graphQLClient: GraphQLClient

    @Test
    fun testGetSatellites() {
        val query = """
            {
              getSatellites {
                id
                name
              }
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(
            query,
            object : TypeReference<List<TestSatelliteDto>>() {}
        )

        assertThat(response, hasSize(14))
        assertThat(
            response, hasItems(
                hasProperty("name", `is`("Moon")),
                hasProperty("name", `is`("Titan"))
            )
        )
    }

    @Test
    fun testGetSatelliteById() {
        val moonId = 1
        val query = """
            {
              getSatellite(id: $moonId) {
                id
                name
              }
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(
            query,
            object : TypeReference<TestSatelliteDto>() {}
        )

        assertThat(
            response, allOf(
                hasProperty("id", `is`(1L)),
                hasProperty("name", `is`("Moon"))
            )
        )
    }
}