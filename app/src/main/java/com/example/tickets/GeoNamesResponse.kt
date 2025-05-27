package com.example.tickets

data class GeoNamesResponse(
    val geonames: List<GeoName>
)

data class GeoName(
    val name: String?,
    val countryName: String?
)

