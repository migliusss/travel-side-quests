package com.example.travelsidequests.data

import jakarta.persistence.*

@Entity
@Table(name = "destination")
class Destination(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    var country: Country,

    @Column(nullable = false, length = 150)
    var name: String,

    @Column(length = 50)
    var type: String? = null,
)
