package com.example.travelsidequests.data

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "side_quest")
class SideQuest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var questId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    var destination: Destination,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(nullable = false, columnDefinition = "text")
    var description: String,

    @Column(length = 300)
    var address: String? = null,

    @Column(nullable = false, precision = 9, scale = 7)
    var latitude: BigDecimal,

    @Column(nullable = false, precision = 9, scale = 7)
    var longitude: BigDecimal,

    @Column(name = "website_url", columnDefinition = "text")
    var websiteUrl: String? = null,
) {
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "side_quest_category",
        joinColumns = [JoinColumn(name = "side_quest_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    var categories: MutableSet<Category> = mutableSetOf()

    @OneToMany(
        mappedBy = "sideQuest",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var sources: MutableList<SideQuestSource> = mutableListOf()

    @OneToMany(
        mappedBy = "sideQuest",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var images: MutableList<SideQuestImage> = mutableListOf()

    fun addImage(imageUrl: String, altText: String? = null) {
        images.add(
            SideQuestImage(
                sideQuest = this,
                imageUrl = imageUrl,
                altText = altText
            )
        )
    }

    fun addSource(name: String, type: SourceType, author: String? = null, url: String? = null, pageNumber: String? = null) {
        sources.add(
            SideQuestSource(
                sideQuest = this,
                name = name,
                type = type,
                author = author,
                url = url,
                pageNumber = pageNumber
            )
        )
    }

    @PrePersist
    fun onCreate() {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
