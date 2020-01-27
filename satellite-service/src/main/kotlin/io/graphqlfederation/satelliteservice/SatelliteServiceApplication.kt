package io.graphqlfederation.satelliteservice

import io.micronaut.runtime.Micronaut

object SatelliteServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.graphqlfederation.satelliteservice")
            .mainClass(SatelliteServiceApplication.javaClass)
            .start()
    }
}
