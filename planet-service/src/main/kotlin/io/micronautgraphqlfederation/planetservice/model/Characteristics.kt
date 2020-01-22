package io.micronautgraphqlfederation.planetservice.model

import javax.persistence.*

@Entity
class Characteristics(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    val planet: Planet,

    @Column
    val meanRadius: Double,

    @Column
    val earthsMass: Double
)