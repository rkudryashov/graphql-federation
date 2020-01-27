package io.graphqlfederation.planetservice.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Property
import io.graphqlfederation.planetservice.web.dto.GraphQLRequest
import io.graphqlfederation.planetservice.web.dto.GraphQLResponse
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Singleton

@Singleton
class GraphQLClient(
    private val objectMapper: ObjectMapper,
    @Property(name = "micronaut.server.port")
    private val port: Int
) {

    private val httpClient = OkHttpClient()
    private val json = "application/json".toMediaType()
    private val url by lazy { "http://localhost:$port/graphql" }

    fun <T> sendRequest(query: String, responseType: TypeReference<T>): T {
        val httpUrl = url.toHttpUrl().newBuilder().build()

        val requestBodyAsString = objectMapper.writeValueAsString(GraphQLRequest(null, query))
            .toRequestBody(json)

        val request = Request.Builder()
            .url(httpUrl)
            .post(requestBodyAsString)
            .build()

        val response = httpClient.newCall(request).execute().also {
            if (it.code != 200) throw RuntimeException("HTTP response isn't OK")
        }

        val responseBody = objectMapper
            .readValue(response.body!!.bytes(), GraphQLResponse::class.java)
            .also { gqlResponse ->
                gqlResponse.errors?.let { errors ->
                    throw RuntimeException("Exception during execution of GraphQL query/mutation: ${errors.map { it.message + "\n" }}")
                }
            }

        val responseData = responseBody.data as Map<String, Any?>

        val payload = when (responseData.size) {
            0 -> throw RuntimeException("No data in graphQL response")
            1 -> responseData.values.first()
            // this is the case in which multiple queries are included in grqphql request. you can try to implement it by yourself
            else -> throw RuntimeException("Not implemented")
        }

        return objectMapper.convertValue(payload, responseType)
    }
}
