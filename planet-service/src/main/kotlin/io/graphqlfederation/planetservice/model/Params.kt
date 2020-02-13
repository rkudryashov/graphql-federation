package io.graphqlfederation.planetservice.model

import java.math.BigDecimal
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

    @Column(precision = 30, scale = 2)
    val mass: BigDecimal
)

@Entity
class InhabitedPlanetParams(
    id: Long = 0,
    meanRadius: Double,
    mass: BigDecimal,
    @Column
    val population: Double
) : Params(id, meanRadius, mass)

@Entity
class UninhabitedPlanetParams(
    id: Long = 0,
    meanRadius: Double,
    mass: BigDecimal
) : Params(id, meanRadius, mass)
