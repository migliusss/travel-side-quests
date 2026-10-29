package com.example.travelsidequests.repository

import com.example.travelsidequests.data.SideQuestImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SideQuestImageRepository : JpaRepository<SideQuestImage, Long>
