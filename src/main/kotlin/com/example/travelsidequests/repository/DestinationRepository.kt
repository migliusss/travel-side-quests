package com.example.travelsidequests.repository

import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.SideQuestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DestinationRepository : JpaRepository<Destination, Long> {

    fun findByCountryCountryIdAndNameIgnoreCase(countryId: Long, name: String): Destination?

    /**
     * Destinations are created by submitters, so one attached only to pending quests
     * must not leak into the public filter.
     */
    @Query(
        """
        select d from Destination d
        where (:countryId is null or d.country.countryId = :countryId)
          and exists (
              select 1 from SideQuest q
              where q.destination = d and q.status = :status
          )
        order by d.name
        """
    )
    fun findAllWithQuests(
        @Param("countryId") countryId: Long?,
        @Param("status") status: SideQuestStatus,
    ): List<Destination>
}
