package com.example.travelsidequests.data

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

/**
 * An image uploaded from the submission form, waiting for the form to be sent.
 * The submitter sees it immediately; the database only keeps it if the submission arrives.
 */
@Entity
@Table(name = "image_upload")
class ImageUpload(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "storage_key", nullable = false, unique = true)
    var storageKey: UUID,

    @Column(name = "content_type", nullable = false, length = 100)
    var contentType: String,

    @Column(nullable = false, columnDefinition = "bytea")
    var content: ByteArray,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),
)
