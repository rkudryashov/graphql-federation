package io.graphqlfederation.authservice.model

import javax.persistence.*

@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val username: String,

    @Column
    val password: String,

    @Column
    val role: String
)
