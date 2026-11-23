package com.example.travelsidequests.dto

data class SideQuestSourceResponse(
    val id: Long,
    val name: String,
    val type: SourceTypeResponse,
    val author: String?,
    val url: String?,
    val pageNumber: String?
)