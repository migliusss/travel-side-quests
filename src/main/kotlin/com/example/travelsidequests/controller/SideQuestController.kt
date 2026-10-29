package com.example.travelsidequests.controller

import com.example.travelsidequests.dto.CreateSideQuestRequest
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestFilterRequest
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.service.CreateSideQuestService
import com.example.travelsidequests.service.GetSideQuestsByFilterService
import com.example.travelsidequests.service.GetSideQuestsService
import com.example.travelsidequests.swagger.SideQuestApi
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/v1/side-quests"])
class SideQuestController(
    val getSideQuestService: GetSideQuestsService,
    val getSideQuestsByFilterService: GetSideQuestsByFilterService,
    val createSideQuestService: CreateSideQuestService,
) : SideQuestApi {

    @GetMapping
    override fun getAll(
        @PageableDefault(size = 10, page = 0, sort = ["questId"])
        pageable: Pageable): PageResponse<SideQuestResponse> {
        return getSideQuestService.execute(pageable)
    }

    @GetMapping("/filter")
    override fun getSideQuestsByFilter(
        filter: SideQuestFilterRequest,
        @PageableDefault(size = 10, page = 0, sort = ["questId"])
        pageable: Pageable): PageResponse<SideQuestResponse> {
        return getSideQuestsByFilterService.execute(filter, pageable,)
    }

    @GetMapping("/{questId}")
    override fun getById(@PathVariable questId: Long): SideQuestResponse {
        return getSideQuestService.getById(questId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(@Valid @RequestBody request: CreateSideQuestRequest): SideQuestResponse {
        return createSideQuestService.execute(request)
    }
}
