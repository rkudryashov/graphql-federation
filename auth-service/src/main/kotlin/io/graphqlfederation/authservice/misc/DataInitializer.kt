package io.graphqlfederation.authservice.misc

import io.graphqlfederation.authservice.service.UserService
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import javax.inject.Singleton

@Singleton
class DataInitializer(
    private val userService: UserService
) {

    @EventListener
    fun init(event: StartupEvent) {
        userService.create("john_doe", "password", "ADMIN")
    }
}