package com.example.travelsidequests.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal

// Anything rendered as a link in the browser has to be checked here: "javascript:..." from an
// open submission form is stored XSS, and a moderator clicking approve is not a defence.
const val WEB_URL_PATTERN = "^https?://\\S+$"

const val STORAGE_KEY_PATTERN =
    "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"

/**
 * One submission creates a side quest and, where needed, the destination it belongs to.
 *
 * Countries and categories are closed lists the caller picks from — letting submitters invent
 * them would fill the public filters with junk long before a moderator saw any of it.
 *
 * Note the explicit `@field:` targets. This module compiles with
 * `-Xannotation-default-target=param-property`, under which a bare constraint lands on the
 * constructor parameter, where Hibernate Validator never looks, and silently does nothing.
 */
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

    @field:Pattern(regexp = WEB_URL_PATTERN, message = "must be an http(s) URL")
    val websiteUrl: String? = null,

    @field:Pattern(regexp = "^[A-Za-z]{2}$", message = "must be a two-letter country code")
    val countryCode: String,

    val destinationId: Long? = null,

    @field:Size(max = 150)
    val destinationName: String? = null,

    @field:Size(max = 50)
    val destinationType: String? = null,

    @field:NotEmpty(message = "pick at least one category")
    val categoryIds: Set<Long>,

    @field:Valid
    @field:Size(max = 10, message = "at most 10 images")
    val images: List<ImageSubmission> = emptyList(),

    @field:Valid
    @field:Size(max = 10, message = "at most 10 sources")
    val sources: List<SourceSubmission> = emptyList(),

    @field:Size(max = 200)
    val submittedBy: String? = null,
) {
    @AssertTrue(message = "give either destinationId or destinationName, not both")
    fun isDestinationChoiceValid(): Boolean =
        (destinationId != null) != !destinationName.isNullOrBlank()
}

data class ImageSubmission(
    @field:Pattern(regexp = STORAGE_KEY_PATTERN, message = "must be an upload key")
    val storageKey: String,

    @field:Size(max = 300)
    val altText: String? = null,
)

data class SourceSubmission(
    @field:NotBlank
    @field:Size(max = 200)
    val name: String,

    val sourceTypeId: Long,

    @field:Pattern(regexp = WEB_URL_PATTERN, message = "must be an http(s) URL")
    val url: String? = null,

    @field:Size(max = 200)
    val author: String? = null,

    @field:Size(max = 50)
    val pageNumber: String? = null,
)
