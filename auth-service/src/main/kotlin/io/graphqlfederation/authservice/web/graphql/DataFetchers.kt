package io.graphqlfederation.authservice.web.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.graphqlfederation.authservice.service.AuthService
import io.graphqlfederation.authservice.web.dto.SignInRequestDto
import io.graphqlfederation.authservice.web.dto.SignInResponseDto
import javax.inject.Singleton

@Singleton
class SignInFetcher(
    private val authService: AuthService,
    private val objectMapper: ObjectMapper
) : DataFetcher<SignInResponseDto> {
    override fun get(env: DataFetchingEnvironment): SignInResponseDto {
        val signInRequest = objectMapper.convertValue(env.getArgument("data"), SignInRequestDto::class.java)
        val username = signInRequest.username
        val token = authService.auth(username, signInRequest.password)
        return SignInResponseDto(username, token)
    }
}

@Singleton
class ValidateTokenFetcher(
    private val authService: AuthService
) : DataFetcher<Boolean> {
    override fun get(env: DataFetchingEnvironment): Boolean {
        val token = env.getArgument<String>("token")
        authService.validateToken(token)
        return true
    }
}
