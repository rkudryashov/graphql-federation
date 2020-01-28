package io.graphqlfederation.authservice

import io.micronaut.runtime.Micronaut

object AuthServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.graphqlfederation.authservice")
            .mainClass(AuthServiceApplication.javaClass)
            .start()
    }
}
