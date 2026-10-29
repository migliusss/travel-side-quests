package com.example.travelsidequests.controller

import com.example.travelsidequests.data.SideQuestStatus
import com.example.travelsidequests.dto.CategoryResponse
import com.example.travelsidequests.dto.CountryResponse
import com.example.travelsidequests.dto.DestinationResponse
import com.example.travelsidequests.dto.SourceTypeResponse
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.CategoryRepository
import com.example.travelsidequests.repository.CountryRepository
import com.example.travelsidequests.repository.DestinationRepository
import com.example.travelsidequests.repository.SourceTypeRepository
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * The lists behind the browse filters and the submission form. Each one is a single
 * repository call, so there is no service layer worth adding.
 */
@RestController
@RequestMapping("/api/v1")
@Transactional(readOnly = true)
class ReferenceDataController(
    private val countryRepository: CountryRepository,
    private val destinationRepository: DestinationRepository,
    private val categoryRepository: CategoryRepository,
    private val sourceTypeRepository: SourceTypeRepository,
) {

    @Operation(
        summary = "List countries.",
        description = "By default only countries that have an approved side quest, which is " +
            "what the browse filter wants. The submission form passes withQuestsOnly=false " +
            "to get the full list."
    )
    @GetMapping("/countries")
    fun countries(
        @RequestParam(defaultValue = "true") withQuestsOnly: Boolean
    ): List<CountryResponse> =
        if (withQuestsOnly) {
            countryRepository.findAllWithQuests(SideQuestStatus.APPROVED).map { it.toResponse() }
        } else {
            countryRepository.findAll(Sort.by("name")).map { it.toResponse() }
        }

    @Operation(
        summary = "List destinations that have an approved side quest.",
        description = "Destinations are created by submitters, so one that exists only " +
            "because of a pending submission is deliberately not listed."
    )
    @GetMapping("/destinations")
    fun destinations(
        @RequestParam(required = false) countryId: Long?
    ): List<DestinationResponse> =
        destinationRepository.findAllWithQuests(countryId, SideQuestStatus.APPROVED)
            .map { it.toResponse() }

    @Operation(summary = "List the categories a submission can be tagged with.")
    @GetMapping("/categories")
    fun categories(): List<CategoryResponse> =
        categoryRepository.findAllByOrderByNameAsc().map { it.toResponse() }

    @Operation(summary = "List the kinds of source a recommendation can come from.")
    @GetMapping("/source-types")
    fun sourceTypes(): List<SourceTypeResponse> =
        sourceTypeRepository.findAllByOrderByNameAsc().map { it.toResponse() }
}
