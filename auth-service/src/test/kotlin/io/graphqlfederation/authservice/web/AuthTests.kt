package io.graphqlfederation.authservice.web

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.graphqlfederation.authservice.service.GraphQLClient
import io.graphqlfederation.authservice.web.dto.SignInResponseDto
import io.micronaut.test.annotation.MicronautTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest
class AuthTests {

    @Inject
    private lateinit var graphQLClient: GraphQLClient
    @Inject
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun testSignIn() {
        val mutation = """
            mutation {
                signIn(data: { username: "john_doe", password: "password" }) {
                    username
                    token
                }
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(mutation, object : TypeReference<SignInResponseDto>() {})

        assertEquals("john_doe", response.username)

        val token = response.token
        val encodedHeader = token.substring(0, token.indexOf("."))
        val encodedPayload = token.substring(token.indexOf(".") + 1, token.lastIndexOf("."))
        val decodedHeader = String(Base64.getDecoder().decode(encodedHeader))
        val decodedPayload = String(Base64.getDecoder().decode(encodedPayload))

        assertEquals(decodedHeader, "{\"alg\":\"HS256\"}")

        val claims = objectMapper.readTree(decodedPayload)
        assertEquals("john_doe", claims["sub"].textValue())
        assertEquals("micronaut", claims["iss"].textValue())
        assertEquals(listOf<Any>(), claims["roles"].toList())
    }

    @Test
    fun testSignInFails() {
        val mutation = """
            mutation {
                signIn(data: { username: "john_doe", password: "password1" }) {
                    username
                    token
                }
            }
        """.trimIndent()

        val exception = assertThrows<RuntimeException>("Should throw an Exception") {
            graphQLClient.sendRequest(mutation, object : TypeReference<SignInResponseDto>() {})
        }

        assertThat(
            exception.message,
            `is`("Exception during execution of GraphQL query/mutation: [Exception while fetching data (/signIn) : Can't authenticate user: john_doe\n]")
        )
    }

    @Test
    fun testValidateIssuedToken() {
        val mutation = """
            mutation {
                signIn(data: { username: "john_doe", password: "password" }) {
                    username
                    token
                }
            }
        """.trimIndent()

        val signInResponse = graphQLClient.sendRequest(mutation, object : TypeReference<SignInResponseDto>() {})

        val query = """
            query {
                validateToken(token: "${signInResponse.token}")
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(
            query,
            object : TypeReference<Boolean>() {}
        )

        assertTrue(response)
    }

    @Test
    fun testValidateIncorrectToken() {
        val query = """
            query {
                validateToken(token: "asdasd")
            }
        """.trimIndent()

        val response = graphQLClient.sendRequest(query, object : TypeReference<Boolean>() {})

        assertFalse(response)
    }
}