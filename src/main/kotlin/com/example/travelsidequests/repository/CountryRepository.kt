package com.example.travelsidequests.repository

import com.example.travelsidequests.data.Country
import com.example.travelsidequests.data.SideQuestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository : JpaRepository<Country, Long> {

    fun findByCountryCodeIgnoreCase(countryCode: String): Country?

    /**
     * The country list is seeded in full, so the public filter shows only the countries
     * that actually have something to show.
     */
    @Query(
        """
        select c from Country c
        where exists (
            select 1 from SideQuest q
            where q.destination.country = c and q.status = :status
        )
        order by c.name
        """
    )
    fun findAllWithQuests(@Param("status") status: SideQuestStatus): List<Country>
}
