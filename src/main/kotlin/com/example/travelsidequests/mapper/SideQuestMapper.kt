package com.example.travelsidequests.mapper

import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.dto.ImageResponse
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.dto.SourceResponse

fun SideQuest.toResponse(): SideQuestResponse =
    SideQuestResponse(
        id = requireNotNull(questId),
        destination = destination.toResponse(),
        name = name,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        websiteUrl = websiteUrl,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        // A MutableSet has no stable iteration order, which would reshuffle the tags
        // in the UI between identical requests.
        categories = categories.sortedBy { it.name }.map { it.toResponse() },
        sources = sources.map { s ->
            SourceResponse(
                id = requireNotNull(s.id),
                name = s.name,
                type = s.type.toResponse(),
                author = s.author,
                url = s.url,
                pageNumber = s.pageNumber
            )
        },
        images = images.map { i ->
            ImageResponse(
                id = requireNotNull(i.id),
                // Uploaded images are served through the API so callers get a stable URL
                // instead of a presigned one that expires.
                imageUrl = i.imageUrl ?: "/api/v1/images/${requireNotNull(i.storageKey)}",
                altText = i.altText
            )
        }
    )
