package com.example.travelsidequests.controller

import com.example.travelsidequests.dto.CategoryResponse
import com.example.travelsidequests.dto.CountryResponse
import com.example.travelsidequests.dto.DestinationResponse
import com.example.travelsidequests.dto.SourceTypeResponse
import com.example.travelsidequests.service.ReferenceDataService
import com.example.travelsidequests.swagger.ReferenceDataApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ReferenceDataController(val referenceDataService: ReferenceDataService) : ReferenceDataApi{

    @GetMapping("/countries")
    override fun getCountries(@RequestParam(defaultValue = "true") withQuestsOnly: Boolean): List<CountryResponse> {
        return referenceDataService.getCountries(withQuestsOnly)
    }

    @GetMapping("/destinations")
    override fun getDestinations(): List<DestinationResponse> {
        return referenceDataService.getDestinations()
    }

    @GetMapping("/categories")
    override fun getCategories(): List<CategoryResponse> {
        return referenceDataService.getCategories()
    }

    @GetMapping("/source-types")
    override fun getSourceTypes(): List<SourceTypeResponse> {
        return referenceDataService.getSourceTypes()
    }
}
