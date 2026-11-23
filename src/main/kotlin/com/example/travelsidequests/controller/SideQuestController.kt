package com.example.travelsidequests.controller

import com.example.travelsidequests.dto.CreateSideQuestRequest
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.service.CreateSideQuestService
import com.example.travelsidequests.service.GetSideQuestsService
import com.example.travelsidequests.swagger.SideQuestApi
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/side-quests"])
class SideQuestController(val getSideQuestService: GetSideQuestsService, val createSideQuestService: CreateSideQuestService) : SideQuestApi {

    @GetMapping
    override fun getSideQuests(
        @RequestParam(required = false) text: String?,
        @RequestParam(required = false) countryId: Long?,
        @RequestParam(required = false) destinationId: Long?,
        @RequestParam(required = false) categoryIds: List<Long>?,
        @RequestParam(required = false) sourceTypeId: Long?,
        @PageableDefault(size = 10, page = 0) pageable: Pageable): PageResponse<SideQuestResponse> {
        return getSideQuestService.getSideQuests(text, countryId, destinationId, categoryIds, sourceTypeId, pageable)
    }

    @GetMapping("/{id}")
    override fun getSideQuestById(@PathVariable id: Long): SideQuestResponse {
        return getSideQuestService.getSideQuestsById(id)
    }

    @PostMapping
    override fun createSideQuest(@Valid @RequestBody request: CreateSideQuestRequest): SideQuestResponse {
        return createSideQuestService.createSideQuest(request)
    }
}
