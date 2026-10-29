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

    /** Set for an external link. Uploaded images leave it null and carry bytes instead. */
    @Column(name = "image_url", columnDefinition = "text")
    var imageUrl: String? = null,

    @Column(name = "content_type", length = 100)
    var contentType: String? = null,

    @Column(name = "alt_text", length = 300)
    var altText: String? = null,
)
