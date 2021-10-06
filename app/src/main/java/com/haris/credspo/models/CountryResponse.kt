package com.haris.credspo.models

data class CountryResponse(
    val data: List<CountryData>
)
data class CountryData(
    val id: Int,
    val name: String
)
