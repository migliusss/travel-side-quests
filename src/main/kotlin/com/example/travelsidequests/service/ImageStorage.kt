package com.example.travelsidequests.service

import java.io.InputStream

/**
 * Uploads land in a staging area and are only promoted once a submission references them,
 * so an abandoned form leaves nothing behind that has to be reaped.
 */
interface ImageStorage {

    /** Stores a freshly uploaded image and returns the key the submission will reference. */
    fun storePending(bytes: ByteArray, contentType: String): String

    /** Moves a staged image into permanent storage. */
    fun promote(key: String)

    fun exists(key: String): Boolean

    fun load(key: String): StoredImage?
}

data class StoredImage(
    val stream: InputStream,
    val contentType: String,
    val size: Long,
)
