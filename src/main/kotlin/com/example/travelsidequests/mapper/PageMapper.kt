package com.example.travelsidequests.mapper

import com.example.travelsidequests.dto.PageResponse
import org.springframework.data.domain.Page

fun <T : Any> Page<T>.toPageResponse(): PageResponse<T> =
    PageResponse(
        content = content,
        page = number,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
        hasNext = hasNext()
    )