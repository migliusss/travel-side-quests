package com.example.travelsidequests.repository

import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.data.SideQuestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SideQuestRepository : JpaRepository<SideQuest, Long>, JpaSpecificationExecutor<SideQuest> {

    fun findAllByStatus(status: SideQuestStatus, pageable: Pageable): Page<SideQuest>

    fun findByQuestIdAndStatus(questId: Long, status: SideQuestStatus): SideQuest?
}
