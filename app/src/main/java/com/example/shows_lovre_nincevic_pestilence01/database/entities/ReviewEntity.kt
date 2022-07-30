package com.example.shows_lovre_nincevic_pestilence01.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shows_lovre_nincevic_pestilence01.models.User



@Entity(tableName = "Review")
data class ReviewEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "rating") val rating: String,
    @ColumnInfo(name = "show_id") val show_id: String,
    @ColumnInfo(name = "user") val user: User
)