package com.example.travelsidequests.data

import jakarta.persistence.*

@Entity
@Table(name = "side_quest_image")
class SideQuestImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "side_quest_id", nullable = false)
    var sideQuest: SideQuest,

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    var imageUrl: String,

    @Column(name = "alt_text", length = 300)
    var altText: String? = null,
)
