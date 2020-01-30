package io.graphqlfederation.authservice.service

import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(
    private val userService: UserService
) : AuthenticationProvider {

    override fun authenticate(authenticationRequest: AuthenticationRequest<Any, Any>): Publisher<AuthenticationResponse> {
        val username = authenticationRequest.identity as String
        val password = authenticationRequest.secret as String
        val user = userService.getByUsername(username)

        return if (password == user.password) {
            Flowable.just(UserDetails(username, listOf()))
        } else Flowable.just(AuthenticationFailed())
    }
}