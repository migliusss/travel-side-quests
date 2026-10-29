package com.example.travelsidequests.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class SideQuestResponse(
    val id: Long,
    val destination: DestinationResponse,
    val name: String,
    val description: String,
    val address: String?,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val websiteUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val categories: List<CategoryResponse>,
    val sources: List<SourceResponse>,
    val images: List<ImageResponse>
)
