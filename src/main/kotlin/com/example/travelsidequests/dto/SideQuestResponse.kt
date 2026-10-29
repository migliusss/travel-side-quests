package com.example.travelsidequests.dto

import com.example.travelsidequests.data.SideQuestStatus
import java.math.BigDecimal
import java.time.Instant

data class SideQuestResponse(
    val id: Long,
    val destination: DestinationResponse,
    val name: String,
    val description: String,
    val address: String?,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val websiteUrl: String?,
    val status: SideQuestStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    val categories: List<CategoryResponse>,
    val sources: List<SourceResponse>,
    val images: List<ImageResponse>
)
