package com.example.travelsidequests.swagger

import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestFilterRequest
import com.example.travelsidequests.dto.SideQuestResponse
import io.swagger.v3.oas.annotations.Operation
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity

interface SideQuestApi {

    @Operation(summary = "Get all paginated side quests.")
    fun getAll(@ParameterObject pageable: Pageable): PageResponse<SideQuestResponse>

    @Operation(summary = "Get paginated side quests by filter.")
    fun getSideQuestsByFilter(
        @ParameterObject filter: SideQuestFilterRequest,
        @ParameterObject pageable: Pageable
    ): PageResponse<SideQuestResponse>

    @Operation(summary = "Create and save a new side quest.")
    fun create() : ResponseEntity<SideQuestResponse>

    @Operation(summary = "Update an existing side quest.")
    fun update() : ResponseEntity<SideQuestResponse>
}