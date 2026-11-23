package com.example.travelsidequests.repository

import com.example.travelsidequests.data.Country
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository : JpaRepository<Country, Long> {

    fun findByCountryCodeIgnoreCase(countryCode: String): Country?

    @Query("""
        SELECT c FROM Country c
        WHERE EXISTS (
            SELECT 1 FROM SideQuest sq WHERE sq.destination.country = c
        )
        ORDER BY c.name ASC
    """)
    fun findAllWithSideQuests(): List<Country>
}
