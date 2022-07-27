package com.example.shows_lovre_nincevic_pestilence01.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.responses.CurrentShowResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.ReviewsResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.ShowsResponse
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentShowDetailsBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Review
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat

class ShowDetailsViewModel: ViewModel() {

    private var _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private var _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context


    fun setParameters(context: Context, id: String, parentActivity: MainActivity){
        this.context = context
        parentActivity.showProgressDialog("Please wait")
        getShow(id)
        parentActivity.hideProgressDialog()
    }

    fun getReviews(id: String) {
        sharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)


        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getReviews(id, accessToken!!, client!!, uid!!).enqueue(object :
            Callback<ReviewsResponse> {
            override fun onResponse(call: Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                if(response.isSuccessful){
                    _reviewsLiveData.value = response.body()!!.reviews.asList()
                    Log.i("IS SUCCESSFUL REVIEW", " YES")
                } else {
                    Log.i("IS SUCCESSFUL REVIEW", " NO")
                }
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                Log.i("FAILURE REVIEW", " YES")
            }


        })

    }

    private fun getShow(id: String) {
        sharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getCurrentShow(id, accessToken!!, client!!, uid!!).enqueue(object :
            Callback<CurrentShowResponse> {
            override fun onResponse(call: Call<CurrentShowResponse>, response: Response<CurrentShowResponse>) {
                if(response.isSuccessful){
                    _showLiveData.value = response.body()!!.show
                    Log.i("IS SUCCESSFUL SHOW", " YES")
                } else {
                    Log.i("IS SUCCESSFUL", " NO")
                }
            }

            override fun onFailure(call: Call<CurrentShowResponse>, t: Throwable) {
                Log.i("FAILURE", " YES")
            }


        })

    }


    }



