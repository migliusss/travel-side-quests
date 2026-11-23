package com.example.travelsidequests.dto

import com.example.travelsidequests.data.DestinationType

data class DestinationResponse(
    val id: Long,
    val country: CountryResponse,
    val name: String,
    val type: DestinationType
)