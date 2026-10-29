package com.example.travelsidequests.data

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Deliberately not mapped as an association on [SideQuestImage]: keeping the bytes
 * off the entity graph is what stops a page of cards from loading every photo.
 */
@Entity
@Table(name = "side_quest_image_content")
class SideQuestImageContent(
    @Id
    @Column(name = "image_id")
    var imageId: Long,

    @Column(nullable = false, columnDefinition = "bytea")
    var content: ByteArray,
)
