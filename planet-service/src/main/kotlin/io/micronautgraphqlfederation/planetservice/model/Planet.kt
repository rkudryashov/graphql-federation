package io.micronautgraphqlfederation.planetservice.model

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
sealed class Planet(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val name: String,

    @Column
    @Enumerated(EnumType.STRING)
    val type: Type,

    @Column
    val characteristicsId: Long
) {
    enum class Type {
        TERRESTRIAL_PLANET,
        GAS_GIANT,
        ICE_GIANT,
        DWARF_PLANET
    }
}

@Entity
class InhabitedPlanet(
    id: Long = 0,
    name: String,
    type: Type,
    characteristicsId: Long,
    val population: Double
) : Planet(id, name, type, characteristicsId)

@Entity
class UninhabitedPlanet(
    id: Long = 0,
    name: String,
    type: Type,
    characteristicsId: Long
) : Planet(id, name, type, characteristicsId)
