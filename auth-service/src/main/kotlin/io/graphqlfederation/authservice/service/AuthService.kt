package io.graphqlfederation.authservice.service

import javax.inject.Singleton

@Singleton
class AuthService(
    private val userService: UserService,
    private val jwtService: JwtService
) {

    /**
     * Returns JWT token if auth succeeds, otherwise throws an exception
     */
    fun auth(username: String, password: String): String {
        val user = userService.getByUsername(username)
        if (password != user.password) throw RuntimeException("Authentication failed!")
        return jwtService.generateToken(username)
    }

    fun validateToken(jws: String) = jwtService.verify(jws)
}
