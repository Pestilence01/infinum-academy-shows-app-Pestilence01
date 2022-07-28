package com.example.shows_lovre_nincevic_pestilence01.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.responses.CurrentShowResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.ReviewsResponse
import com.example.shows_lovre_nincevic_pestilence01.models.Review
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private var _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private var _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context

    private lateinit var parentActivity: MainActivity


    fun setParameters(context: Context, id: String, parentActivity: MainActivity) {
        this.context = context
        sharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        getShow(id)
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
                    _reviewsLiveData.value = response.body()!!.reviews.asList()
                } else {
                    parentActivity.showErrorSnackBar(
                        "You need to sign in or sign up before continuing.",
                        true
                    )
                }
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                parentActivity.showErrorSnackBar("Oops! Something went wrong!", true)
            }


        })

    }

    private fun getShow(id: String) {

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
                    _showLiveData.value = response.body()!!.show
                } else {
                    parentActivity.showErrorSnackBar("We couldn't find this show!", true)
                }
            }

            override fun onFailure(call: Call<CurrentShowResponse>, t: Throwable) {
                parentActivity.showErrorSnackBar("Oops! Something went wrong!", true)
            }


        })

    }


}



