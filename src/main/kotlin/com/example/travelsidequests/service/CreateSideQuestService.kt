package com.example.travelsidequests.service

import com.example.travelsidequests.data.Category
import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.DestinationType
import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.dto.CreateSideQuestRequest
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.exceptionhandler.BadRequestException
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateSideQuestService(
    private val sideQuestRepository: SideQuestRepository,
    private val countryRepository: CountryRepository,
    private val destinationRepository: DestinationRepository,
    private val categoryRepository: CategoryRepository,
    private val sourceTypeRepository: SourceTypeRepository
) {

    @Transactional
    fun createSideQuest(request: CreateSideQuestRequest): SideQuestResponse {
        val destination = resolveDestination(request)
        val categories = resolveCategories(request.categoryIds)

        val sideQuest = SideQuest(
            destination = destination,
            name = request.name.trim(),
            description = request.description.trim(),
            address = request.address?.trim()?.ifBlank { null },
            latitude = request.latitude,
            longitude = request.longitude
        )
        sideQuest.categories.addAll(categories)

        if (request.sources.isNotEmpty()) {
            val typeIds = request.sources.map { it.id }.toSet()
            val typesById = sourceTypeRepository.findAllById(typeIds).associateBy { it.id }

            request.sources.forEach { s ->
                val type = typesById[s.id]
                    ?: throw BadRequestException("Source type with id=${s.id} not found.")

                sideQuest.addSource(
                    name = s.name.trim(),
                    type = type,
                    author = s.author?.trim()?.ifBlank { null },
                    url = s.url?.trim()?.ifBlank { null },
                    pageNumber = s.pageNumber?.trim()?.ifBlank { null }
                )
            }
        }

        return sideQuestRepository.save(sideQuest).toResponse()
    }

    private fun resolveDestination(request: CreateSideQuestRequest): Destination {
        if ((request.destinationId != null) != (!request.destinationName.isNullOrBlank())) throw BadRequestException("give either destinationId or destinationName, not both")

        val country = countryRepository.findByCountryCodeIgnoreCase(request.countryCode.trim())
            ?: throw BadRequestException("No country with countryCode=${request.countryCode}.")

        request.destinationId?.let { destinationId ->
            val existingDestination = destinationRepository.findById(destinationId).orElseThrow {
                BadRequestException("No destination with destinationId=$destinationId.")
            }

            if (existingDestination.country.id != country.id) {
                throw BadRequestException(
                    "Destination with destinationId=$destinationId is not in countryCode=${request.countryCode}."
                )
            }

            return existingDestination
        }

        val cleanName = request.destinationName?.trim()?.ifBlank { null }
            ?: throw BadRequestException("destinationName is required when destinationId is not provided.")

        val rawType = request.destinationType?.toString()?.trim()?.uppercase()
            ?: throw BadRequestException("destinationType is required when creating a new destination.")

        val destinationType = try {
            DestinationType.valueOf(rawType)
        } catch (_: IllegalArgumentException) {
            throw BadRequestException("Invalid destinationType=$rawType.")
        }

        return destinationRepository.findByCountryIdAndNameIgnoreCase(country.id!!, cleanName)
            ?: destinationRepository.save(
                Destination(
                    country = country,
                    name = cleanName,
                    type = destinationType
                )
            )
    }

    private fun resolveCategories(categoryIds: Set<Long>): List<Category> {
        val categories = categoryRepository.findAllById(categoryIds)

        if (categories.size != categoryIds.size) {
            val found = categories.mapNotNull { it.id }.toSet()

            throw BadRequestException("Some unknown categories categoryIds= ${(categoryIds - found).sorted()}.")
        }

        return categories
    }
}
