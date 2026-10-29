package com.example.travelsidequests.service

import com.example.travelsidequests.data.Category
import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.ImageUpload
import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.data.SideQuestImageContent
import com.example.travelsidequests.data.SideQuestStatus
import com.example.travelsidequests.dto.CreateSideQuestRequest
import com.example.travelsidequests.dto.ImageSubmission
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.exceptionhandler.BadRequestException
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.CategoryRepository
import com.example.travelsidequests.repository.CountryRepository
import com.example.travelsidequests.repository.DestinationRepository
import com.example.travelsidequests.repository.ImageUploadRepository
import com.example.travelsidequests.repository.SideQuestImageContentRepository
import com.example.travelsidequests.repository.SideQuestRepository
import com.example.travelsidequests.repository.SourceTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreateSideQuestService(
    private val sideQuestRepository: SideQuestRepository,
    private val countryRepository: CountryRepository,
    private val destinationRepository: DestinationRepository,
    private val categoryRepository: CategoryRepository,
    private val sourceTypeRepository: SourceTypeRepository,
    private val imageUploadRepository: ImageUploadRepository,
    private val imageContentRepository: SideQuestImageContentRepository,
) {

    @Transactional
    fun execute(request: CreateSideQuestRequest): SideQuestResponse {
        val destination = resolveDestination(request)
        val categories = resolveCategories(request.categoryIds)
        val uploads = resolveUploads(request.images)

        val quest = SideQuest(
            destination = destination,
            name = request.name.trim(),
            description = request.description.trim(),
            address = request.address?.trim()?.ifBlank { null },
            latitude = request.latitude,
            longitude = request.longitude,
            websiteUrl = request.websiteUrl?.trim()?.ifBlank { null },
            status = SideQuestStatus.PENDING,
            submittedBy = request.submittedBy?.trim()?.ifBlank { null },
        )

        quest.categories.addAll(categories)

        uploads.forEach { (submission, upload) ->
            quest.addUploadedImage(upload.contentType, submission.altText?.trim()?.ifBlank { null })
        }

        request.sources.forEach { source ->
            val type = sourceTypeRepository.findById(source.sourceTypeId).orElseThrow {
                BadRequestException("No source type with id ${source.sourceTypeId}.")
            }

            quest.addSource(
                name = source.name.trim(),
                type = type,
                author = source.author?.trim()?.ifBlank { null },
                url = source.url?.trim()?.ifBlank { null },
                pageNumber = source.pageNumber?.trim()?.ifBlank { null },
            )
        }

        // Flush so the cascaded images and sources have ids before anything references them.
        val saved = sideQuestRepository.saveAndFlush(quest)

        // The image rows were added in upload order, so they line up.
        saved.images.zip(uploads).forEach { (image, pair) ->
            imageContentRepository.save(
                SideQuestImageContent(
                    imageId = requireNotNull(image.id),
                    content = pair.second.content,
                )
            )
        }

        imageUploadRepository.deleteAll(uploads.map { it.second })

        return saved.toResponse()
    }

    private fun resolveUploads(
        submissions: List<ImageSubmission>
    ): List<Pair<ImageSubmission, ImageUpload>> =
        submissions.map { submission ->
            val key = runCatching { UUID.fromString(submission.storageKey) }.getOrNull()
                ?: throw BadRequestException("${submission.storageKey} is not an upload key.")

            val upload = imageUploadRepository.findByStorageKey(key)
                ?: throw BadRequestException(
                    "Image ${submission.storageKey} was not uploaded, or the upload expired."
                )

            submission to upload
        }

    private fun resolveDestination(request: CreateSideQuestRequest): Destination {
        val country = countryRepository.findByCountryCodeIgnoreCase(request.countryCode)
            ?: throw BadRequestException("No country with code ${request.countryCode}.")

        request.destinationId?.let { destinationId ->
            val existing = destinationRepository.findById(destinationId).orElseThrow {
                BadRequestException("No destination with id $destinationId.")
            }

            if (existing.country.countryId != country.countryId) {
                throw BadRequestException(
                    "Destination $destinationId is not in ${request.countryCode}."
                )
            }

            return existing
        }

        // Validated by @AssertTrue: exactly one of destinationId and destinationName is set.
        val name = requireNotNull(request.destinationName).trim()

        destinationRepository
            .findByCountryCountryIdAndNameIgnoreCase(requireNotNull(country.countryId), name)
            ?.let { return it }

        // Two people naming the same new destination at the same moment is rare enough that
        // the unique index rejecting the loser, surfaced as a 409, is the right trade here.
        return destinationRepository.save(
            Destination(
                country = country,
                name = name,
                type = request.destinationType?.trim()?.ifBlank { null },
            )
        )
    }

    private fun resolveCategories(categoryIds: Set<Long>): List<Category> {
        val categories = categoryRepository.findAllById(categoryIds)

        if (categories.size != categoryIds.size) {
            val found = categories.mapNotNull { it.categoryId }.toSet()
            throw BadRequestException("Unknown category ids: ${(categoryIds - found).sorted()}.")
        }

        return categories
    }
}
