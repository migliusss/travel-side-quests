package com.example.travelsidequests.service

import com.example.travelsidequests.data.SideQuest
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
import java.time.Instant

@Service
class ModerationService(
    private val sideQuestRepository: SideQuestRepository
) {

    @Transactional(readOnly = true)
    fun list(status: SideQuestStatus, pageable: Pageable): PageResponse<SideQuestResponse> =
        sideQuestRepository.findAllByStatus(status, pageable)
            .map { it.toResponse() }
            .toPageResponse()

    @Transactional
    fun approve(questId: Long): SideQuestResponse {
        val quest = find(questId)

        quest.status = SideQuestStatus.APPROVED
        quest.rejectionReason = null
        quest.moderatedAt = Instant.now()

        return quest.toResponse()
    }

    @Transactional
    fun reject(questId: Long, reason: String?): SideQuestResponse {
        val quest = find(questId)

        quest.status = SideQuestStatus.REJECTED
        quest.rejectionReason = reason?.trim()?.ifBlank { null }
        quest.moderatedAt = Instant.now()

        return quest.toResponse()
    }

    private fun find(questId: Long): SideQuest =
        sideQuestRepository.findById(questId).orElseThrow {
            NotFoundException("No side quest with id $questId.")
        }
}
