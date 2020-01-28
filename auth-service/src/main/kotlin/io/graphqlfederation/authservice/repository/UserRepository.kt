package io.graphqlfederation.authservice.repository

import io.graphqlfederation.authservice.model.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByUsername(name: String): User?
}
