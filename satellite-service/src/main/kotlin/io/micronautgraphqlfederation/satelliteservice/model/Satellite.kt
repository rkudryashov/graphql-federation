package io.micronautgraphqlfederation.satelliteservice.model

import javax.persistence.*

@Entity
class Satellite(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val name: String,

    @Column
    val planetId: Long
)