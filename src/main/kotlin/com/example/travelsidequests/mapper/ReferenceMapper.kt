package com.example.travelsidequests.mapper

import com.example.travelsidequests.data.Category
import com.example.travelsidequests.data.Country
import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.SourceType
import com.example.travelsidequests.dto.CategoryResponse
import com.example.travelsidequests.dto.CountryResponse
import com.example.travelsidequests.dto.DestinationResponse
import com.example.travelsidequests.dto.SourceTypeResponse

fun Country.toResponse(): CountryResponse =
    CountryResponse(
        id = requireNotNull(countryId),
        name = name,
        countryCode = countryCode
    )

fun Destination.toResponse(): DestinationResponse =
    DestinationResponse(
        id = requireNotNull(id),
        country = country.toResponse(),
        name = name,
        type = type
    )

fun Category.toResponse(): CategoryResponse =
    CategoryResponse(
        id = requireNotNull(categoryId),
        name = name
    )

fun SourceType.toResponse(): SourceTypeResponse =
    SourceTypeResponse(
        id = requireNotNull(id),
        name = name
    )
