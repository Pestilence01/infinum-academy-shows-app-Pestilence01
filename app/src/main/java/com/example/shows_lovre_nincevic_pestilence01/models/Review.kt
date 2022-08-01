package com.example.shows_lovre_nincevic_pestilence01.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    @SerialName("id") val id: String,
    @SerialName("comment") val comment: String?,
    @SerialName("rating") val rating: String,
    @SerialName("show_id") val show_id: String,
    @SerialName("user") val user: User?
)