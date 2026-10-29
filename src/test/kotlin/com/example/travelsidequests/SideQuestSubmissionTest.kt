package com.example.travelsidequests

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SideQuestSubmissionTest : AbstractIntegrationTest() {

    @Test
    fun `a submission is invisible until a moderator approves it`() {
        val storageKey = upload()

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
            .andReturn()

        val questId = created.id()
        val imageUrl = JsonPath.read<String>(created.response.contentAsString, "$.images[0].imageUrl")

        assertFalse(
            publicQuestNames().contains("Dynjandi waterfall"),
            "pending quest leaked into the public list"
        )

        mockMvc.perform(get("/api/v1/side-quests/$questId"))
            .andExpect(status().isNotFound)

        // The bytes survived the move out of the staging table.
        mockMvc.perform(get(imageUrl))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.IMAGE_PNG))

        mockMvc.perform(post("/api/v1/admin/side-quests/$questId/approve").asAdmin())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("APPROVED"))

        mockMvc.perform(get("/api/v1/side-quests/$questId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Dynjandi waterfall"))
    }

    @Test
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
                .asAdmin()
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
        // Also the canary for the param-property annotation default: if the constraints
        // ever stop landing on the field, Hibernate Validator sees nothing and this is 201.
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
    fun `a file that is not an image is rejected`() {
        val notAnImage = MockMultipartFile(
            "file", "payload.png", MediaType.IMAGE_PNG_VALUE, "<?php echo 1; ?>".toByteArray()
        )

        mockMvc.perform(multipart("/api/v1/images").file(notAnImage))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `the admin queue requires authentication`() {
        mockMvc.perform(get("/api/v1/admin/side-quests"))
            .andExpect(status().isUnauthorized)
    }

    /** Uploads a real PNG and returns its key. */
    private fun upload(): String {
        val png = ByteArrayOutputStream()
            .also { ImageIO.write(BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB), "png", it) }
            .toByteArray()

        val file = MockMultipartFile("file", "photo.png", MediaType.IMAGE_PNG_VALUE, png)

        return mockMvc.perform(multipart("/api/v1/images").file(file))
            .andExpect(status().isCreated)
            .andReturn()
            .let { JsonPath.read(it.response.contentAsString, "$.storageKey") }
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

    private fun MockHttpServletRequestBuilder.asAdmin() = with(user("admin").roles("ADMIN"))
}
