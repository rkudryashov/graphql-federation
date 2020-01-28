package io.graphqlfederation.authservice.web.dto

class SignInRequestDto(
    val username: String,
    val password: String
)

class SignInResponseDto(
    val username: String,
    val token: String
)
