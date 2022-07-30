package com.example.shows_lovre_nincevic_pestilence01.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shows_lovre_nincevic_pestilence01.database.entities.ReviewEntity
import com.example.shows_lovre_nincevic_pestilence01.database.entities.ShowEntity

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE show_id is :showId")
    fun getAllReviews(showId: String) : LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM review WHERE id IS :reviewId")
    fun getReview (reviewId: String): LiveData<ReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReview(review: ReviewEntity)
}