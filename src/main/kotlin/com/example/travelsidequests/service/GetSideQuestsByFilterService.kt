package com.example.travelsidequests.service

import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestFilterRequest
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.mapper.toPageResponse
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.SideQuestRepository
import com.example.travelsidequests.repository.SideQuestSpecifications
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetSideQuestsByFilterService(
    private val sideQuestRepository: SideQuestRepository
) {
    @Transactional(readOnly = true)
    fun execute(
        filter: SideQuestFilterRequest,
        pageable: Pageable,
    ): PageResponse<SideQuestResponse> {

        val spec = Specification.allOf(
            listOfNotNull(
                // Whatever else the caller asks for, only approved submissions are public.
                SideQuestSpecifications.approved(),
                filter.q
                    ?.takeIf { it.isNotBlank() }
                    ?.let { SideQuestSpecifications.matchesText(it) },
                filter.countryId?.let { SideQuestSpecifications.inCountry(it) },
                filter.destinationId?.let { SideQuestSpecifications.inDestination(it) },
                filter.categoryIds
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { SideQuestSpecifications.inAnyCategory(it) },
                filter.sourceTypeId?.let { SideQuestSpecifications.citedBySourceType(it) },
            )
        )

        return sideQuestRepository.findAll(spec, pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }
}
