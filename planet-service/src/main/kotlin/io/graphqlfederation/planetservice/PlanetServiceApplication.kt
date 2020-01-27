package io.graphqlfederation.planetservice

import io.micronaut.runtime.Micronaut

object PlanetServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.graphqlfederation.planetservice")
            .mainClass(PlanetServiceApplication.javaClass)
            .start()
    }
}
