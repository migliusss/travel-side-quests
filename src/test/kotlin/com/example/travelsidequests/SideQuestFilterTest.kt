package com.example.travelsidequests

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertTrue

class SideQuestFilterTest : AbstractIntegrationTest() {

    /**
     * This filter used to throw: the specification read a `source` attribute that
     * `SideQuest` does not have, and would have reached the source's own id rather than
     * its type even if it did.
     */
    @Test
    fun `filtering by source type returns the quests citing it`() {
        val bookTypeId = sourceTypeId("BOOK")

        mockMvc.perform(get("/api/v1/side-quests/filter").param("sourceTypeId", bookTypeId.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].sources[0].type.name").value("BOOK"))
    }

    @Test
    fun `filtering by a source type nothing cites returns nothing`() {
        val podcastTypeId = sourceTypeId("PODCAST")

        mockMvc.perform(get("/api/v1/side-quests/filter").param("sourceTypeId", podcastTypeId.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(0))
    }

    @Test
    fun `the country filter narrows to that country`() {
        val icelandId = JsonPath.read<List<Int>>(
            mockMvc.perform(get("/api/v1/countries")).andReturn().response.contentAsString,
            "$[?(@.countryCode == 'IS')].id"
        ).first()

        mockMvc.perform(get("/api/v1/side-quests/filter").param("countryId", icelandId.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].destination.country.countryCode").value("IS"))
    }

    @Test
    fun `the public country list only offers countries that have something to show`() {
        val codes: List<String> = JsonPath.read(
            mockMvc.perform(get("/api/v1/countries")).andReturn().response.contentAsString,
            "$[*].countryCode"
        )

        assertTrue(codes.contains("IS"))
        assertTrue(codes.size < 50, "expected the filtered list, got what looks like all ${codes.size} countries")
    }

    @Test
    fun `the submission form can still ask for every country`() {
        val codes: List<String> = JsonPath.read(
            mockMvc.perform(get("/api/v1/countries").param("withQuestsOnly", "false"))
                .andReturn().response.contentAsString,
            "$[*].countryCode"
        )

        assertTrue(codes.size > 200, "expected the full ISO list, got ${codes.size}")
    }

    private fun sourceTypeId(name: String): Long {
        val body = mockMvc.perform(get("/api/v1/source-types"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        return JsonPath.read<List<Int>>(body, "$[?(@.name == '$name')].id").first().toLong()
    }
}
