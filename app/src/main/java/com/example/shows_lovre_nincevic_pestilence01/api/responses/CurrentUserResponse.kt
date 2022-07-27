package com.example.shows_lovre_nincevic_pestilence01.api.responses

import com.example.shows_lovre_nincevic_pestilence01.models.User
import kotlinx.serialization.SerialName

data class CurrentUserResponse(
    @SerialName("user") val user: User
)