package com.example.travelsidequests.service

import com.example.travelsidequests.data.ImageUpload
import com.example.travelsidequests.exceptionhandler.NotFoundException
import com.example.travelsidequests.repository.ImageUploadRepository
import com.example.travelsidequests.repository.SideQuestImageContentRepository
import com.example.travelsidequests.repository.SideQuestImageRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class ImageService(
    private val imageUploadRepository: ImageUploadRepository,
    private val sideQuestImageRepository: SideQuestImageRepository,
    private val imageContentRepository: SideQuestImageContentRepository,
    private val imageProcessor: ImageProcessor,
) {
    private val log = LoggerFactory.getLogger(ImageService::class.java)

    @Transactional
    fun upload(bytes: ByteArray): UUID {
        val normalized = imageProcessor.normalize(bytes)
        val key = UUID.randomUUID()

        imageUploadRepository.save(
            ImageUpload(
                storageKey = key,
                contentType = normalized.contentType,
                content = normalized.bytes,
            )
        )

        sweepAbandonedUploads()

        return key
    }

    @Transactional(readOnly = true)
    fun load(imageId: Long): LoadedImage {
        val image = sideQuestImageRepository.findById(imageId).orElseThrow {
            NotFoundException("No image with id $imageId.")
        }

        val content = imageContentRepository.findById(imageId).orElseThrow {
            NotFoundException("No image with id $imageId.")
        }

        return LoadedImage(
            bytes = content.content,
            contentType = image.contentType ?: "application/octet-stream",
        )
    }

    /**
     * Uploads only matter until the form that references them is sent, so anything
     * older than a day belonged to a submission nobody finished.
     */
    private fun sweepAbandonedUploads() {
        val removed = imageUploadRepository.deleteOlderThan(Instant.now().minus(RETENTION))

        if (removed > 0) log.info("removed {} abandoned uploads.", removed)
    }

    companion object {
        private val RETENTION: Duration = Duration.ofDays(1)
    }
}

class LoadedImage(
    val bytes: ByteArray,
    val contentType: String,
)
