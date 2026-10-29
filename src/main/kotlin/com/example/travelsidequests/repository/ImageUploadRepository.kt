package com.example.travelsidequests.repository

import com.example.travelsidequests.data.ImageUpload
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface ImageUploadRepository : JpaRepository<ImageUpload, Long> {

    fun findByStorageKey(storageKey: UUID): ImageUpload?

    fun existsByStorageKey(storageKey: UUID): Boolean

    @Modifying
    @Query("delete from ImageUpload u where u.createdAt < :before")
    fun deleteOlderThan(@Param("before") before: Instant): Int
}
