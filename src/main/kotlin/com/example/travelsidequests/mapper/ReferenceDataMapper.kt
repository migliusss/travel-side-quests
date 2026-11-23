package com.example.travelsidequests.mapper

import com.example.travelsidequests.data.Category
import com.example.travelsidequests.data.Country
import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.SideQuestImage
import com.example.travelsidequests.data.SideQuestSource
import com.example.travelsidequests.data.SourceType
import com.example.travelsidequests.dto.*

fun Destination.toResponse(): DestinationResponse =
    DestinationResponse(
        id = requireNotNull(id),
        country = country.toResponse(),
        name = name,
        type = type
    )

fun Country.toResponse(): CountryResponse =
    CountryResponse(
        id = requireNotNull(id),
        name = name,
        countryCode = countryCode
    )

fun Category.toResponse(): CategoryResponse =
    CategoryResponse(
        id = requireNotNull(id),
        name = name
    )

fun SideQuestSource.toResponse(): SideQuestSourceResponse =
    SideQuestSourceResponse(
        id = requireNotNull(id),
        name = name,
        type = type.toResponse(),
        author = author,
        url = url,
        pageNumber = pageNumber
    )

fun SourceType.toResponse(): SourceTypeResponse =
    SourceTypeResponse(
        id = requireNotNull(id),
        name = name
    )

fun SideQuestImage.toResponse(): SideQuestImageResponse =
    SideQuestImageResponse(
        id = requireNotNull(id),
        imageUrl = imageUrl,
        altText = altText
    )
