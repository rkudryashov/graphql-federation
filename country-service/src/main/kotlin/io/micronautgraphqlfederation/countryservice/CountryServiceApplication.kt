package io.micronautgraphqlfederation.countryservice

import io.micronaut.runtime.Micronaut

object CountryServiceApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("io.micronautgraphqlfederation.countryservice")
            .mainClass(CountryServiceApplication.javaClass)
            .start()
    }
}
