package com.haris.credspo.models

data class ActivityHistoryResponse(
    val message: String,
    val data: ActivityHistoryResponseData
)
data class ActivityHistoryResponseData(
    val badges: List<BadgeModel>,
    val activities: MutableList<ActivityModel>
)