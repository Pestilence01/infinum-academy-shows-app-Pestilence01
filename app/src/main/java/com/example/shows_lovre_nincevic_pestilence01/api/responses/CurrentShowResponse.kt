package com.example.shows_lovre_nincevic_pestilence01.api.responses

import com.example.shows_lovre_nincevic_pestilence01.models.Show
import kotlinx.serialization.SerialName

data class CurrentShowResponse(
    @SerialName("show") val show: Show
)