package com.example.travelsidequests.repository

import com.example.travelsidequests.data.Category
import com.example.travelsidequests.data.Country
import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.data.SideQuestSource
import com.example.travelsidequests.data.SideQuestStatus
import com.example.travelsidequests.data.SourceType
import org.springframework.data.jpa.domain.Specification

/**
 * Every public read composes [approved]. Keeping it in one place is convenience;
 * the safeguard is the integration test asserting a pending quest is invisible.
 */
object SideQuestSpecifications {

    private const val LIKE_ESCAPE = '\\'

    fun approved(): Specification<SideQuest> = hasStatus(SideQuestStatus.APPROVED)

    fun hasStatus(status: SideQuestStatus): Specification<SideQuest> =
        Specification { root, _, cb -> cb.equal(root.get<SideQuestStatus>("status"), status) }

    /**
     * What the search box on the home page sends. Matches the place, what is written about
     * it, and where it is, so "fjord", "Iceland" and "Westfjords" all find something.
     */
    fun matchesText(text: String): Specification<SideQuest> =
        Specification { root, _, cb ->
            val pattern = "%${escapeForLike(text.trim().lowercase())}%"
            val destination = root.get<Destination>("destination")

            cb.or(
                cb.like(cb.lower(root.get("name")), pattern, LIKE_ESCAPE),
                cb.like(cb.lower(root.get("description")), pattern, LIKE_ESCAPE),
                cb.like(cb.lower(destination.get("name")), pattern, LIKE_ESCAPE),
                cb.like(cb.lower(destination.get<Country>("country").get("name")), pattern, LIKE_ESCAPE),
            )
        }

    /** Otherwise a search for "100%" or "_" would match far more than the user meant. */
    private fun escapeForLike(text: String): String =
        text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")

    fun inCountry(countryId: Long): Specification<SideQuest> =
        Specification { root, _, cb ->
            cb.equal(
                root.get<Destination>("destination").get<Country>("country").get<Long>("countryId"),
                countryId
            )
        }

    fun inDestination(destinationId: Long): Specification<SideQuest> =
        Specification { root, _, cb ->
            cb.equal(root.get<Destination>("destination").get<Long>("id"), destinationId)
        }

    fun inAnyCategory(categoryIds: Collection<Long>): Specification<SideQuest> =
        Specification { root, query, _ ->
            query.distinct(true)

            root.join<SideQuest, Category>("categories")
                .get<Long>("categoryId")
                .`in`(categoryIds)
        }

    fun citedBySourceType(sourceTypeId: Long): Specification<SideQuest> =
        Specification { root, query, cb ->
            query.distinct(true)

            cb.equal(
                root.join<SideQuest, SideQuestSource>("sources")
                    .get<SourceType>("type")
                    .get<Long>("id"),
                sourceTypeId
            )
        }
}
