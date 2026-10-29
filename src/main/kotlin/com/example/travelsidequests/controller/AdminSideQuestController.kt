package com.example.travelsidequests.controller

import com.example.travelsidequests.data.SideQuestStatus
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.RejectSideQuestRequest
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.service.ModerationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/side-quests")
@SecurityRequirement(name = "basicAuth")
class AdminSideQuestController(
    private val moderationService: ModerationService
) {

    @Operation(summary = "List submissions awaiting review, or any other status.")
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "PENDING") status: SideQuestStatus,
        @ParameterObject @PageableDefault(size = 20, page = 0, sort = ["createdAt"])
        pageable: Pageable,
    ): PageResponse<SideQuestResponse> = moderationService.list(status, pageable)

    @Operation(summary = "Approve a submission, making it public.")
    @PostMapping("/{questId}/approve")
    fun approve(@PathVariable questId: Long): SideQuestResponse =
        moderationService.approve(questId)

    @Operation(summary = "Reject a submission, optionally recording why.")
    @PostMapping("/{questId}/reject")
    fun reject(
        @PathVariable questId: Long,
        @RequestBody(required = false) request: RejectSideQuestRequest?,
    ): SideQuestResponse = moderationService.reject(questId, request?.reason)
}
