package com.example.travelsidequests.repository

import com.example.travelsidequests.data.SourceType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SourceTypeRepository : JpaRepository<SourceType, Long> {

    fun findAllByOrderByNameAsc(): List<SourceType>
}
