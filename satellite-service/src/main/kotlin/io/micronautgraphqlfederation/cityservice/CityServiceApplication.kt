package io.micronautgraphqlfederation.cityservice

import io.micronaut.runtime.Micronaut

object CityServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.micronautgraphqlfederation.cityservice")
            .mainClass(CityServiceApplication.javaClass)
            .start()
    }
}
