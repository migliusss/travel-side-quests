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

    // Exactly one of imageUrl (external link) and storageKey (uploaded object) is set;
    // the database enforces it with a check constraint.
    @Column(name = "image_url", columnDefinition = "text")
    var imageUrl: String? = null,

    @Column(name = "storage_key", columnDefinition = "text")
    var storageKey: String? = null,

    @Column(name = "alt_text", length = 300)
    var altText: String? = null,
)
