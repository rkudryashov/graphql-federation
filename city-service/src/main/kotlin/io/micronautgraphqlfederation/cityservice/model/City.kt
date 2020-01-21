package io.micronautgraphqlfederation.cityservice.model

import javax.persistence.*

@Entity
class City(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val name: String
)