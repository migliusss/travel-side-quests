package com.example.travelsidequests.service

import com.example.travelsidequests.data.Category
import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.data.SideQuestStatus
import com.example.travelsidequests.dto.CreateSideQuestRequest
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.exceptionhandler.BadRequestException
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.CategoryRepository
import com.example.travelsidequests.repository.CountryRepository
import com.example.travelsidequests.repository.DestinationRepository
import com.example.travelsidequests.repository.SideQuestRepository
import com.example.travelsidequests.repository.SourceTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateSideQuestService(
    private val sideQuestRepository: SideQuestRepository,
    private val countryRepository: CountryRepository,
    private val destinationRepository: DestinationRepository,
    private val categoryRepository: CategoryRepository,
    private val sourceTypeRepository: SourceTypeRepository,
    private val imageStorage: ImageStorage,
) {

    @Transactional
    fun execute(request: CreateSideQuestRequest): SideQuestResponse {
        val destination = resolveDestination(request)
        val categories = resolveCategories(request.categoryIds)

        request.images.forEach { image ->
            if (!imageStorage.exists(image.storageKey)) {
                throw BadRequestException(
                    "Image ${image.storageKey} was not uploaded, or the upload expired."
                )
            }
        }

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

        request.images.forEach { quest.addUploadedImage(it.storageKey, it.altText?.trim()) }

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

        // Flush so the cascaded images and sources have ids before they are mapped.
        val saved = sideQuestRepository.saveAndFlush(quest)

        request.images.forEach { imageStorage.promote(it.storageKey) }

        return saved.toResponse()
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
