package io.micronautgraphqlfederation.satelliteservice

import io.micronaut.runtime.Micronaut

object SatelliteServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.micronautgraphqlfederation.satelliteservice")
            .mainClass(SatelliteServiceApplication.javaClass)
            .start()
    }
}
