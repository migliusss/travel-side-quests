package com.example.travelsidequests.repository

import com.example.travelsidequests.data.SideQuest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SideQuestRepository : JpaRepository<SideQuest, Long>, JpaSpecificationExecutor<SideQuest> {}
