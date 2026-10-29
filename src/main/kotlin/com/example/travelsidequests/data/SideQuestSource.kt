package com.example.travelsidequests.data

import jakarta.persistence.*

@Entity
@Table(name = "side_quest_source")
class SideQuestSource(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "side_quest_id", nullable = false)
    var sideQuest: SideQuest,

    @Column(nullable = false, length = 200)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_type_id", nullable = false)
    var type: SourceType,

    @Column(length = 200)
    var author: String? = null,

    @Column(columnDefinition = "text")
    var url: String? = null,

    @Column(name = "page_number", columnDefinition = "text")
    var pageNumber: String? = null,
) {}
