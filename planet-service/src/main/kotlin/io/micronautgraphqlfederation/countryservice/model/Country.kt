package io.micronautgraphqlfederation.countryservice.model

import javax.persistence.*

@Entity
class Country(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val name: String
)