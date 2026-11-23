package com.example.travelsidequests.data

import jakarta.persistence.*

@Entity
@Table(name = "country")
class Country(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(
        name = "country_code",
        nullable = false,
        unique = true,
        length = 2
    )
    var countryCode: String,
)