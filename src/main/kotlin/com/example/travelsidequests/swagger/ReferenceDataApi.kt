package com.example.travelsidequests.swagger

import com.example.travelsidequests.dto.CategoryResponse
import com.example.travelsidequests.dto.CountryResponse
import com.example.travelsidequests.dto.DestinationResponse
import com.example.travelsidequests.dto.SourceTypeResponse
import io.swagger.v3.oas.annotations.Operation
import org.springdoc.core.annotations.ParameterObject

interface ReferenceDataApi {

    @Operation(summary = "List all the countries.")
    fun getCountries(@ParameterObject withQuestsOnly: Boolean): List<CountryResponse>

    @Operation(summary = "List the destinations.")
    fun getDestinations(): List<DestinationResponse>

    @Operation(summary = "List the categories.")
    fun getCategories(): List<CategoryResponse>

    @Operation(summary = "List the kinds of source a recommendation can come from.")
    fun getSourceTypes(): List<SourceTypeResponse>
}