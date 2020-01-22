package io.micronautgraphqlfederation.planetservice.model

import javax.persistence.*

@Entity
class Planet(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val name: String,

    @Column
    @Enumerated(EnumType.STRING)
    val type: Type
) {
    enum class Type {
        TERRESTRIAL_PLANET,
        GAS_GIANT,
        ICE_GIANT,
        DWARF_PLANET
    }
}