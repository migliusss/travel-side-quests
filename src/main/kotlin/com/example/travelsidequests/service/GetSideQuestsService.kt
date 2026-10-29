package com.example.travelsidequests.service

import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.mapper.toPageResponse
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.SideQuestRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetSideQuestsService(
    private val sideQuestRepository: SideQuestRepository
) {
    fun execute(pageable: Pageable): PageResponse<SideQuestResponse> {
        return sideQuestRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }
}