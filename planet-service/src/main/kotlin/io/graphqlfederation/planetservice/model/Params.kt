package io.graphqlfederation.planetservice.model

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(length = 32)
sealed class Params(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val meanRadius: Double,

    @Column
    val earthsMass: Double
)

@Entity
class InhabitedPlanetParams(
    id: Long = 0,
    meanRadius: Double,
    earthsMass: Double,
    @Column
    val population: Double
) : Params(id, meanRadius, earthsMass)

@Entity
class UninhabitedPlanetParams(
    id: Long = 0,
    meanRadius: Double,
    earthsMass: Double
) : Params(id, meanRadius, earthsMass)
