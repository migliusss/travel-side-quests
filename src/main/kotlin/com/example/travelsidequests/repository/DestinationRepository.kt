package com.example.travelsidequests.repository

import com.example.travelsidequests.data.Destination
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DestinationRepository : JpaRepository<Destination, Long> {
    fun findByCountryIdAndNameIgnoreCase(countryId: Long, name: String): Destination?
}
