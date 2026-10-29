package com.example.travelsidequests.service

import com.example.travelsidequests.data.SideQuestStatus
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.exceptionhandler.NotFoundException
import com.example.travelsidequests.mapper.toPageResponse
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.SideQuestRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetSideQuestsService(
    private val sideQuestRepository: SideQuestRepository
) {
    @Transactional(readOnly = true)
    fun execute(pageable: Pageable): PageResponse<SideQuestResponse> {
        return sideQuestRepository.findAllByStatus(SideQuestStatus.APPROVED, pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    @Transactional(readOnly = true)
    fun getById(questId: Long): SideQuestResponse {
        return sideQuestRepository.findByQuestIdAndStatus(questId, SideQuestStatus.APPROVED)
            ?.toResponse()
            ?: throw NotFoundException("No side quest with id $questId.")
    }
}
