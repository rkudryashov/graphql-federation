package io.graphqlfederation.planetservice.service

import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.repository.PlanetRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Singleton

@Singleton
class PlanetService(
    private val repository: PlanetRepository,
    private val paramsService: ParamsService
) {

    private val publishSubject = PublishSubject.create<Planet>()

    fun getAll(): Iterable<Planet> = repository.findAll()

    fun getById(id: Long): Planet = repository
        .findById(id)
        .orElseThrow { RuntimeException("Can't find planet by id=$id") }

    fun getByName(name: String): Planet = repository.findByName(name)
        ?: throw RuntimeException("Can't find planet by name=$name")

    fun create(
        name: String,
        type: Planet.Type,
        meanRadius: Double,
        massNumber: Double,
        massTenPower: Int,
        population: Double? = null
    ): Planet {
        fun createBigDecimal(number: Double, tenPower: Int) = number.toBigDecimal().multiply(BigDecimal.TEN.pow(tenPower))

        val params = paramsService.create(meanRadius, createBigDecimal(massNumber, massTenPower), population)

        return Planet(name = name, type = type, paramsId = params.id).also {
            repository.save(it)
            publishSubject.onNext(it)
        }
    }

    fun getLatestPlanet(): Flowable<Planet> = publishSubject.toFlowable(BackpressureStrategy.BUFFER)
}
