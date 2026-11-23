package com.example.travelsidequests.data

import jakarta.persistence.*

@Entity
@Table(name = "category_type")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    var name: String,
)
