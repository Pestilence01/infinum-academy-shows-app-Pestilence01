package com.example.shows_lovre_nincevic_pestilence01.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.responses.CurrentShowResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.ReviewsResponse
import com.example.shows_lovre_nincevic_pestilence01.database.ShowsDatabase
import com.example.shows_lovre_nincevic_pestilence01.database.entities.ReviewEntity
import com.example.shows_lovre_nincevic_pestilence01.database.entities.ShowEntity
import com.example.shows_lovre_nincevic_pestilence01.models.Review
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class ShowDetailsViewModel(
    private val database: ShowsDatabase
) : ViewModel() {

    private var _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private var _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context

    private lateinit var parentActivity: MainActivity


    fun setParameters(context: Context, id: String, parentActivity: MainActivity) {
        this.context = context
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if(parentActivity.isOnline()) {
            getShow(id)
            getReviews(id)
        }
    }

    fun setShow(show: Show){
        _showLiveData.value = show
    }

    fun getShowFromDB(show_id: String): LiveData<ShowEntity> {
        return database.showDao().getShow(show_id)
    }

    fun getReviewsFromDB(show_id: String): LiveData<List<ReviewEntity>> {
        return database.reviewDao().getAllReviews(show_id)
    }

    fun setReviews(reviews: List<Review>) {
        _reviewsLiveData.value = reviews
    }

    fun getReviews(id: String) {


        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getReviews(id, accessToken!!, client!!, uid!!).enqueue(object :
            Callback<ReviewsResponse> {
            override fun onResponse(
                call: Call<ReviewsResponse>,
                response: Response<ReviewsResponse>
            ) {
                if (response.isSuccessful) {
                    setReviews(response.body()!!.reviews.asList())
                    Executors.newSingleThreadExecutor().execute{
                        putReviewsInDB()
                    }
                } else {
                    parentActivity.showErrorSnackBar(
                        Constants.SIGN_BEFORE_CONTINUING,
                        true
                    )
                }
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                parentActivity.showErrorSnackBar(Constants.SOMETHING_WRONG, true)
            }


        })

    }

    private fun putReviewsInDB() {
        val reviewList = _reviewsLiveData.value
        val iterator = reviewList!!.iterator()
        while(iterator.hasNext()){
            val review = iterator.next()
            val reviewEntity = ReviewEntity(id = review.id, comment = review.comment, rating = review.rating, show_id = review.show_id, user = review.user!!)
            database.reviewDao().insertReview(reviewEntity)
        }
    }

    fun getShow(id: String) {

        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getCurrentShow(id, accessToken!!, client!!, uid!!).enqueue(object :
            Callback<CurrentShowResponse> {
            override fun onResponse(
                call: Call<CurrentShowResponse>,
                response: Response<CurrentShowResponse>
            ) {
                if (response.isSuccessful) {
                    setShow(response.body()!!.show)
                } else {
                    parentActivity.showErrorSnackBar(Constants.CANNOT_FIND_SHOW, true)
                }
            }

            override fun onFailure(call: Call<CurrentShowResponse>, t: Throwable) {
                parentActivity.showErrorSnackBar(Constants.SOMETHING_WRONG, true)
            }


        })

    }


}



