package com.example.shows_lovre_nincevic_pestilence01.api.responses

import com.example.shows_lovre_nincevic_pestilence01.models.Review
import kotlinx.serialization.SerialName

data class ReviewsResponse (
    @SerialName("reviews") val reviews: Array<Review>
    )

