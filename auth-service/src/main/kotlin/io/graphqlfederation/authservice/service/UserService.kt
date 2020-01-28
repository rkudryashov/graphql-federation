package io.graphqlfederation.authservice.service

import io.graphqlfederation.authservice.model.User
import io.graphqlfederation.authservice.repository.UserRepository
import javax.inject.Singleton

@Singleton
class UserService(
    private val repository: UserRepository
) {

    fun create(username: String, password: String, role: String) = User(
        username = username,
        password = password,
        role = role
    ).also { repository.save(it) }

    fun getByUsername(username: String): User = repository.findByUsername(username)
        ?: throw RuntimeException("Can't find user by username=$username")
}