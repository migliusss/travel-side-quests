package com.example.travelsidequests.dto

import com.example.travelsidequests.data.DestinationType
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateSideQuestRequest(
    @field:NotBlank
    @field:Size(max = 200)
    val name: String,

    @field:NotBlank
    @field:Size(max = 5000)
    val description: String,

    @field:Size(max = 300)
    val address: String? = null,

    @field:DecimalMin("-90.0")
    @field:DecimalMax("90.0")
    val latitude: BigDecimal,

    @field:DecimalMin("-180.0")
    @field:DecimalMax("180.0")
    val longitude: BigDecimal,

    @field:Pattern(regexp = "^https?://\\S+$", message = "must be an http(s) URL")
    val websiteUrl: String? = null,

    @field:NotBlank
    @field:Pattern(regexp = "^[A-Za-z]{2}$", message = "must be a two-letter country code")
    val countryCode: String,

    val destinationId: Long? = null,

    @field:Size(max = 150)
    val destinationName: String? = null,

    val destinationType: DestinationType? = null,

    @field:NotEmpty(message = "pick at least one category")
    val categoryIds: Set<Long>,

    @field:Size(max = 10, message = "at most 10 sources")
    val sources: List<SideQuestSourceRequest> = emptyList(),

    @field:Size(max = 200)
    val submittedBy: String? = null,
) {}
