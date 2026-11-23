package com.example.travelsidequests.swagger

import com.example.travelsidequests.dto.CreateSideQuestRequest
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestResponse
import io.swagger.v3.oas.annotations.Operation
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable

interface SideQuestApi {

    @Operation(summary = "Get all paginated side quests or by filter.")
    fun getSideQuests(
        @ParameterObject text: String?,
        @ParameterObject countryId: Long?,
        @ParameterObject destinationId: Long?,
        @ParameterObject categoryIds: List<Long>?,
        @ParameterObject sourceTypeId: Long?,
        @ParameterObject pageable: Pageable): PageResponse<SideQuestResponse>

    @Operation(summary = "Get a single side quest.")
    fun getSideQuestById(id: Long): SideQuestResponse

    @Operation(summary = "Submit a new side quest.")
    fun createSideQuest(request: CreateSideQuestRequest): SideQuestResponse
}
