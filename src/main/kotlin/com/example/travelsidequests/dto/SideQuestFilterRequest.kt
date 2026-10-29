package com.example.travelsidequests.dto

data class SideQuestFilterRequest(
    val countryId: Long? = null,
    val destinationId: Long? = null,
    val categoryIds: List<Long>? = null,
    val sourceTypeId: Long? = null,
)
