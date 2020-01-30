package io.graphqlfederation.authservice.service

import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.Authenticator
import io.micronaut.security.authentication.UserDetails
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.generator.AccessRefreshTokenGenerator
import io.micronaut.security.token.jwt.validator.JwtTokenValidator
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class AuthService(
    private val authenticator: Authenticator,
    private val accessRefreshTokenGenerator: AccessRefreshTokenGenerator,
    private val jwtTokenValidator: JwtTokenValidator
) {

    fun authenticate(username: String, password: String): Publisher<String> {
        val authenticationResponseFlowable =
            Flowable.fromPublisher(authenticator.authenticate(UsernamePasswordCredentials(username, password)))

        return authenticationResponseFlowable.map { authenticationResponse ->
            if (authenticationResponse.isAuthenticated) {
                accessRefreshTokenGenerator.generate(UserDetails(username, listOf()))
                    .orElseThrow { RuntimeException("Can't get token") }
                    .accessToken
            } else {
                throw AuthenticationException("Can't authenticate user: $username")
            }
        }
    }

    fun validateToken(jws: String) = jwtTokenValidator.validate(jws)
}
