package com.example.shows_lovre_nincevic_pestilence01.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostReviewRequest(
    @SerialName("rating") val rating: String,
    @SerialName("comment") val comment: String,
    @SerialName("show_id") val show_id: String
)