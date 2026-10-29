package com.example.travelsidequests.dto

data class DestinationResponse(
    val id: Long,
    val country: CountryResponse,
    val name: String,
    val type: String?
)