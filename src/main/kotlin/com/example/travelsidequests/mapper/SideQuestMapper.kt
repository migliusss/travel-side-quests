package com.example.travelsidequests.mapper

import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.dto.CategoryResponse
import com.example.travelsidequests.dto.CountryResponse
import com.example.travelsidequests.dto.DestinationResponse
import com.example.travelsidequests.dto.ImageResponse
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.dto.SourceResponse
import com.example.travelsidequests.dto.SourceTypeResponse

fun SideQuest.toResponse(): SideQuestResponse =
    SideQuestResponse(
        id = requireNotNull(questId),
        destination = DestinationResponse(
            id = requireNotNull(destination.id),
            country = CountryResponse(
                id = requireNotNull(destination.country.countryId),
                name = destination.country.name,
                countryCode = destination.country.countryCode
            ),
            name = destination.name,
            type = destination.type
        ),
        name = name,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        websiteUrl = websiteUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        categories = categories.map { c ->
            CategoryResponse(
                id = requireNotNull(c.categoryId),
                name = c.name
            )
        },
        sources = sources.map { s ->
            SourceResponse(
                id = requireNotNull(s.id),
                name = s.name,
                type = SourceTypeResponse(
                    id = requireNotNull(s.type.id),
                    name = s.type.name
                ),
                author = s.author,
                url = s.url,
                pageNumber = s.pageNumber
            )
        },
        images = images.map { i ->
            ImageResponse(
                id = requireNotNull(i.id),
                imageUrl = i.imageUrl,
                altText = i.altText
            )
        }
    )
