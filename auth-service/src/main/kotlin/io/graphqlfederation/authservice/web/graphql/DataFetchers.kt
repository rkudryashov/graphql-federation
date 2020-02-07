package io.graphqlfederation.authservice.web.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.graphqlfederation.authservice.service.AuthService
import io.graphqlfederation.authservice.web.dto.SignInRequestDto
import io.graphqlfederation.authservice.web.dto.SignInResponseDto
import io.reactivex.Flowable
import javax.inject.Singleton

@Singleton
class SignInDataFetcher(
    private val authService: AuthService,
    private val objectMapper: ObjectMapper
) : DataFetcher<SignInResponseDto> {

    override fun get(env: DataFetchingEnvironment): SignInResponseDto {
        val signInRequest = objectMapper.convertValue(env.getArgument("data"), SignInRequestDto::class.java)
        val username = signInRequest.username
        val tokenPublisher = authService.authenticate(username, signInRequest.password)
        return Flowable.fromPublisher(tokenPublisher)
            .map { token -> SignInResponseDto(username, token) }
            // graphql-java doesn't support rxjava, so it is needed to block
            .blockingSingle()
    }
}

@Singleton
class ValidateTokenDataFetcher(
    private val authService: AuthService
) : DataFetcher<Boolean> {
    override fun get(env: DataFetchingEnvironment): Boolean {
        val token = env.getArgument<String>("token")
        return authService.validateToken(token)
    }
}
