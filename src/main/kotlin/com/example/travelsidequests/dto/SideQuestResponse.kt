package com.example.travelsidequests.dto

import java.math.BigDecimal
import java.time.Instant

data class SideQuestResponse(
    val id: Long,
    val name: String,
    val description: String,
    val address: String?,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val createdAt: Instant,
    val updatedAt: Instant,
    val destination: DestinationResponse,
    val categories: List<CategoryResponse>,
    val sources: List<SideQuestSourceResponse>,
    val images: List<SideQuestImageResponse>
)
