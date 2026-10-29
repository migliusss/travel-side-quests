package com.example.travelsidequests.dto

data class SideQuestFilterRequest(
    /** Free text, matched against the name, description, destination and country. */
    val q: String? = null,
    val countryId: Long? = null,
    val destinationId: Long? = null,
    val categoryIds: List<Long>? = null,
    val sourceTypeId: Long? = null,
)
