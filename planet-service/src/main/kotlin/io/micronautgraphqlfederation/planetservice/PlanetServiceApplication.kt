package io.micronautgraphqlfederation.planetservice

import io.micronaut.runtime.Micronaut

object PlanetServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.micronautgraphqlfederation.planetservice")
            .mainClass(PlanetServiceApplication.javaClass)
            .start()
    }
}
