package com.example.shows_lovre_nincevic_pestilence01.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val comment: String,
    val username: String,
    val rating: Int,
    @DrawableRes val profileImageBitmap: Bitmap?,
) : Parcelable