package com.example.shows_lovre_nincevic_pestilence01

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Show(
    val ID: String,
    val title: String,
    val description: String,
    @DrawableRes val imageResourceID: Int,
    val reviews: MutableList<Review>
) : Parcelable