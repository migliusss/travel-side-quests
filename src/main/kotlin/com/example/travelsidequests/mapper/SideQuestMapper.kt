package com.example.travelsidequests.mapper

import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.dto.SideQuestResponse

fun SideQuest.toResponse(): SideQuestResponse =
    SideQuestResponse(
        id = requireNotNull(id),
        name = name,
        address = address,
        description = description,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt,
        updatedAt = updatedAt,
        destination = destination.toResponse(),
        categories = categories.map { c -> c.toResponse() },
        sources = sources.map { s -> s.toResponse() },
        images = images.map { i -> i.toResponse() },
    )
