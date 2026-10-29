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

    fun approved(): Specification<SideQuest> = hasStatus(SideQuestStatus.APPROVED)

    fun hasStatus(status: SideQuestStatus): Specification<SideQuest> =
        Specification { root, _, cb -> cb.equal(root.get<SideQuestStatus>("status"), status) }

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
