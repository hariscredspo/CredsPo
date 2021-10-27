package com.haris.credspo.models

data class UserBadgesResponse(
    val message: String,
    val data: List<BadgeData>
)
data class BadgeData(
    val id: Int,
    val name: String,
    val count: Int
)