package com.example.travelsidequests.repository

import com.example.travelsidequests.data.SideQuestImageContent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SideQuestImageContentRepository : JpaRepository<SideQuestImageContent, Long>
