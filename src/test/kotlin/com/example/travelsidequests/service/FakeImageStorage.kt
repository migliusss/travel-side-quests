package com.example.travelsidequests.service

import java.io.ByteArrayInputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The reason [ImageStorage] is an interface: the tests get the whole upload lifecycle
 * without a MinIO container.
 */
class FakeImageStorage : ImageStorage {

    private val objects = ConcurrentHashMap<String, Stored>()

    val promoted: MutableSet<String> = ConcurrentHashMap.newKeySet()

    override fun storePending(bytes: ByteArray, contentType: String): String {
        val key = UUID.randomUUID().toString()
        objects[key] = Stored(bytes, contentType)
        return key
    }

    override fun promote(key: String) {
        promoted.add(key)
    }

    override fun exists(key: String): Boolean = objects.containsKey(key)

    override fun load(key: String): StoredImage? =
        objects[key]?.let {
            StoredImage(
                stream = ByteArrayInputStream(it.bytes),
                contentType = it.contentType,
                size = it.bytes.size.toLong(),
            )
        }

    fun clear() {
        objects.clear()
        promoted.clear()
    }

    private class Stored(val bytes: ByteArray, val contentType: String)
}
