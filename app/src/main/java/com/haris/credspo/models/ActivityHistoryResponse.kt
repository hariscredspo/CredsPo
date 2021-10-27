package com.haris.credspo.models

data class ActivityHistoryResponse(
    val message: String,
    val data: ActivityHistoryResponseData
)
data class ActivityHistoryResponseData(
    val badges: List<ActivityHistoryResponseBadgeData>,
    val activities: MutableList<ActivityModel>
)
data class ActivityHistoryResponseBadgeData(
    val id: Int,
    val name: String
)