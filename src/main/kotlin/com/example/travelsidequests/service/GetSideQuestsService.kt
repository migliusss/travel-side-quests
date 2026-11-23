package com.example.travelsidequests.service

import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.exceptionhandler.NotFoundException
import com.example.travelsidequests.mapper.toPageResponse
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.SideQuestRepository
import com.example.travelsidequests.repository.SideQuestSpecifications
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetSideQuestsService(
    private val sideQuestRepository: SideQuestRepository
) {
    @Transactional(readOnly = true)
    fun getSideQuests(
        text: String?,
        countryId: Long?,
        destinationId: Long?,
        categoryIds: List<Long>?,
        sourceTypeId: Long?,
        pageable: Pageable
    ): PageResponse<SideQuestResponse> {
        val spec = buildSpecification(text, countryId, destinationId, categoryIds, sourceTypeId)

        return sideQuestRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    @Transactional(readOnly = true)
    fun getSideQuestsById(questId: Long): SideQuestResponse {
        return sideQuestRepository.findById(questId)
            .map { it.toResponse() }
            .orElseThrow { NotFoundException("Side quest with questId=$questId was not found.") }
    }

    private fun buildSpecification(
        text: String?,
        countryId: Long?,
        destinationId: Long?,
        categoryIds: List<Long>?,
        sourceTypeId: Long?
    ): Specification<SideQuest> {
        var spec: Specification<SideQuest> = Specification.unrestricted()

        if (!text.isNullOrBlank()) {
            spec = spec.and(SideQuestSpecifications.matchesText(text))
        }
        if (countryId != null) {
            spec = spec.and(SideQuestSpecifications.inCountry(countryId))
        }
        if (destinationId != null) {
            spec = spec.and(SideQuestSpecifications.inDestination(destinationId))
        }
        if (!categoryIds.isNullOrEmpty()) {
            spec = spec.and(SideQuestSpecifications.inAnyCategory(categoryIds))
        }
        if (sourceTypeId != null) {
            spec = spec.and(SideQuestSpecifications.citedBySourceType(sourceTypeId))
        }

        return spec
    }
}
