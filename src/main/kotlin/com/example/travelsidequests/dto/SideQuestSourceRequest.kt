package com.example.travelsidequests.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SideQuestSourceRequest(
    @field:NotBlank
    @field:Size(max = 200)
    val name: String,

    val id: Long,

    @field:Pattern(regexp = "^https?://\\S+$", message = "must be an http(s) URL")
    val url: String? = null,

    @field:Size(max = 200)
    val author: String? = null,

    @field:Size(max = 20)
    val pageNumber: String? = null
)
