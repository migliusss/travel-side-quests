package com.example.travelsidequests.data

import jakarta.persistence.*

@Entity
@Table(name = "category")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var categoryId: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    var name: String,
)
