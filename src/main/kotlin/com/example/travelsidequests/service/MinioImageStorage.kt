package com.example.travelsidequests.service

import com.example.travelsidequests.config.MinioProperties
import io.minio.CopyObjectArgs
import io.minio.CopySource
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.StatObjectArgs
import io.minio.errors.ErrorResponseException
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.util.UUID

@Component
class MinioImageStorage(
    private val client: MinioClient,
    private val properties: MinioProperties,
) : ImageStorage {

    override fun storePending(bytes: ByteArray, contentType: String): String {
        val key = UUID.randomUUID().toString()

        ByteArrayInputStream(bytes).use { stream ->
            client.putObject(
                PutObjectArgs.builder()
                    .bucket(properties.bucket)
                    .`object`(pendingName(key))
                    .stream(stream, bytes.size.toLong(), -1)
                    .contentType(contentType)
                    .build()
            )
        }

        return key
    }

    override fun promote(key: String) {
        if (statOrNull(storedName(key)) != null) return

        client.copyObject(
            CopyObjectArgs.builder()
                .bucket(properties.bucket)
                .`object`(storedName(key))
                .source(
                    CopySource.builder()
                        .bucket(properties.bucket)
                        .`object`(pendingName(key))
                        .build()
                )
                .build()
        )

        client.removeObject(
            RemoveObjectArgs.builder()
                .bucket(properties.bucket)
                .`object`(pendingName(key))
                .build()
        )
    }

    override fun exists(key: String): Boolean =
        statOrNull(storedName(key)) != null || statOrNull(pendingName(key)) != null

    override fun load(key: String): StoredImage? {
        val name = listOf(storedName(key), pendingName(key)).firstOrNull { statOrNull(it) != null }
            ?: return null

        val stat = statOrNull(name) ?: return null

        val stream = client.getObject(
            GetObjectArgs.builder()
                .bucket(properties.bucket)
                .`object`(name)
                .build()
        )

        return StoredImage(
            stream = stream,
            contentType = stat.contentType() ?: "application/octet-stream",
            size = stat.size(),
        )
    }

    private fun statOrNull(name: String) =
        try {
            client.statObject(
                StatObjectArgs.builder()
                    .bucket(properties.bucket)
                    .`object`(name)
                    .build()
            )
        } catch (ex: ErrorResponseException) {
            if (ex.errorResponse().code() in NOT_FOUND_CODES) null else throw ex
        }

    private fun pendingName(key: String) = "$PENDING_PREFIX$key"

    private fun storedName(key: String) = "$STORED_PREFIX$key"

    companion object {
        const val PENDING_PREFIX = "pending/"
        const val STORED_PREFIX = "quests/"

        private val NOT_FOUND_CODES = setOf("NoSuchKey", "NoSuchObject", "ResourceNotFound")
    }
}
