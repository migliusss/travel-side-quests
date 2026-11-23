package com.example.travelsidequests.service

import com.example.travelsidequests.dto.CategoryResponse
import com.example.travelsidequests.dto.CountryResponse
import com.example.travelsidequests.dto.DestinationResponse
import com.example.travelsidequests.dto.SourceTypeResponse
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.CategoryRepository
import com.example.travelsidequests.repository.CountryRepository
import com.example.travelsidequests.repository.DestinationRepository
import com.example.travelsidequests.repository.SourceTypeRepository
import org.springframework.stereotype.Service

@Service
class ReferenceDataService(
    val countryRepository: CountryRepository,
    val destinationRepository: DestinationRepository,
    val categoryRepository: CategoryRepository,
    val sourceTypeRepository: SourceTypeRepository,) {

    fun getCountries(withQuestsOnly: Boolean): List<CountryResponse> {
        val countries =
            if (withQuestsOnly) {
                countryRepository.findAllWithSideQuests()
            } else {
                countryRepository.findAll()
            }

        return countries.map { it.toResponse() }
    }

    fun getDestinations(): List<DestinationResponse> {
        return destinationRepository.findAll().map { it.toResponse() }
    }

    fun getCategories(): List<CategoryResponse> {
        return categoryRepository.findAll().map { it.toResponse() }
    }

    fun getSourceTypes(): List<SourceTypeResponse> {
        return sourceTypeRepository.findAll().map { it.toResponse() }
    }
}