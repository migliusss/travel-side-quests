package com.example.travelsidequests

import com.example.travelsidequests.service.FakeImageStorage
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SideQuestSubmissionTest : AbstractIntegrationTest() {

    @Test
    fun `a submission is invisible until a moderator approves it`() {
        val storageKey = (imageStorage as FakeImageStorage).storePending(ByteArray(8), "image/jpeg")

        val created = mockMvc.perform(
            post("/api/v1/side-quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    submission(
                        name = "Dynjandi waterfall",
                        destinationName = "Arnarfjörður",
                        images = """[{"storageKey": "$storageKey", "altText": "The falls"}]"""
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.images[0].imageUrl").value("/api/v1/images/$storageKey"))
            .andReturn()

        val questId = created.id()

        assertFalse(publicQuestNames().contains("Dynjandi waterfall"), "pending quest leaked into the public list")

        mockMvc.perform(get("/api/v1/side-quests/$questId"))
            .andExpect(status().isNotFound)

        // The uploaded object is moved out of staging once something references it.
        assertTrue((imageStorage as FakeImageStorage).promoted.contains(storageKey))

        mockMvc.perform(post("/api/v1/admin/side-quests/$questId/approve").withAdmin())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("APPROVED"))

        mockMvc.perform(get("/api/v1/side-quests/$questId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Dynjandi waterfall"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `a rejected submission records why and stays private`() {
        val questId = mockMvc.perform(
            post("/api/v1/side-quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(submission(name = "Spam listing", destinationName = "Nowhere"))
        )
            .andExpect(status().isCreated)
            .andReturn()
            .id()

        mockMvc.perform(
            post("/api/v1/admin/side-quests/$questId/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"reason": "Advertising"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("REJECTED"))

        assertFalse(publicQuestNames().contains("Spam listing"))
    }

    @Test
    fun `an existing destination is reused whatever the casing`() {
        val response = mockMvc.perform(
            post("/api/v1/side-quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(submission(name = "Hidden pool", destinationName = "westfjords"))
        )
            .andExpect(status().isCreated)
            .andReturn()
            .let { JsonPath.parse(it.response.contentAsString) }

        // The seeded Westfjords destination, not a second one spelled differently.
        assertEquals("Westfjords", response.read<String>("$.destination.name"))
    }

    @Test
    fun `a blank name is rejected with the offending field named`() {
        // Also the canary for the -Xannotation-default-target=param-property compiler flag:
        // if the constraints ever stop being applied, this returns 201.
        mockMvc.perform(
            post("/api/v1/side-quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(submission(name = "  ", destinationName = "Somewhere"))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.name").exists())
    }

    @Test
    fun `a javascript url is rejected`() {
        mockMvc.perform(
            post("/api/v1/side-quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    submission(
                        name = "Looks fine",
                        destinationName = "Somewhere",
                        websiteUrl = "javascript:alert(1)"
                    )
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.websiteUrl").exists())
    }

    @Test
    fun `naming both an existing and a new destination is rejected`() {
        mockMvc.perform(
            post("/api/v1/side-quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    submission(name = "Confused", destinationName = "Somewhere")
                        .replace(""""categoryIds"""", """"destinationId": 1, "categoryIds"""")
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.destinationChoiceValid").exists())
    }

    @Test
    fun `an image key that was never uploaded is rejected`() {
        mockMvc.perform(
            post("/api/v1/side-quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    submission(
                        name = "No such image",
                        destinationName = "Somewhere",
                        images = """[{"storageKey": "11111111-2222-3333-4444-555555555555"}]"""
                    )
                )
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `the admin queue requires authentication`() {
        mockMvc.perform(get("/api/v1/admin/side-quests"))
            .andExpect(status().isUnauthorized)
    }

    private fun submission(
        name: String,
        destinationName: String,
        websiteUrl: String? = null,
        images: String = "[]",
    ): String {
        val websiteLine = websiteUrl?.let { """"websiteUrl": "$it",""" } ?: ""

        return """
            {
              "name": "$name",
              "description": "Somewhere worth the detour.",
              "latitude": 65.5,
              "longitude": -23.5,
              $websiteLine
              "countryCode": "IS",
              "destinationName": "$destinationName",
              "categoryIds": [${firstCategoryId()}],
              "images": $images,
              "sources": []
            }
        """.trimIndent()
    }

    private fun firstCategoryId(): Long =
        mockMvc.perform(get("/api/v1/categories"))
            .andExpect(status().isOk)
            .andReturn()
            .let { JsonPath.read<Int>(it.response.contentAsString, "$[0].id").toLong() }

    private fun publicQuestNames(): List<String> =
        mockMvc.perform(get("/api/v1/side-quests").param("size", "100"))
            .andExpect(status().isOk)
            .andReturn()
            .let { JsonPath.read(it.response.contentAsString, "$.content[*].name") }

    private fun MvcResult.id(): Long =
        JsonPath.read<Int>(response.contentAsString, "$.id").toLong()
}

private fun org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.withAdmin() =
    with(
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
            .user("admin").roles("ADMIN")
    )
