package com.example.shows_lovre_nincevic_pestilence01.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Show(
    @SerialName("id") val id: String,
    @SerialName("average_rating") val average_rating: String?,
    @SerialName("description") val description: String?,
    @SerialName("image_url") val image_url: String,
    @SerialName("no_of_reviews") val no_of_reviews: String,
    @SerialName("title") val title: String
)