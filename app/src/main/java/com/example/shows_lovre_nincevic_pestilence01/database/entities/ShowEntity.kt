package com.example.shows_lovre_nincevic_pestilence01.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants


@Entity(tableName = Constants.SHOW)
data class ShowEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "average_rating") val average_rating: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "image_url") val image_url: String,
    @ColumnInfo(name = "no_of_reviews") val no_of_reviews: String,
    @ColumnInfo(name = "title") val title: String
)