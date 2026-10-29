package com.example.travelsidequests.swagger

import com.example.travelsidequests.dto.CreateSideQuestRequest
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestFilterRequest
import com.example.travelsidequests.dto.SideQuestResponse
import io.swagger.v3.oas.annotations.Operation
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable

interface SideQuestApi {

    @Operation(summary = "Get all paginated side quests.")
    fun getAll(@ParameterObject pageable: Pageable): PageResponse<SideQuestResponse>

    @Operation(summary = "Get paginated side quests by filter.")
    fun getSideQuestsByFilter(
        @ParameterObject filter: SideQuestFilterRequest,
        @ParameterObject pageable: Pageable
    ): PageResponse<SideQuestResponse>

    @Operation(summary = "Get a single side quest.")
    fun getById(questId: Long): SideQuestResponse

    @Operation(
        summary = "Submit a new side quest.",
        description = "Anyone may submit. The submission is held for review and is not " +
            "returned by the public endpoints until a moderator approves it."
    )
    fun create(request: CreateSideQuestRequest): SideQuestResponse
}
