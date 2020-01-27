package io.graphqlfederation.planetservice.model

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(length = 32)
sealed class Characteristics(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val meanRadius: Double,

    @Column
    val earthsMass: Double
)

@Entity
class InhabitedPlanetCharacteristics(
    id: Long = 0,
    meanRadius: Double,
    earthsMass: Double,
    val population: Double
) : Characteristics(id, meanRadius, earthsMass)

@Entity
class UninhabitedPlanetCharacteristics(
    id: Long = 0,
    meanRadius: Double,
    earthsMass: Double
) : Characteristics(id, meanRadius, earthsMass)
