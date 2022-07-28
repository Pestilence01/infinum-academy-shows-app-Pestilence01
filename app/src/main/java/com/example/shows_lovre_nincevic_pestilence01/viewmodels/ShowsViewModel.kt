package com.example.shows_lovre_nincevic_pestilence01.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.responses.CurrentUserResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.ShowsResponse
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShowsViewModel: ViewModel() {

    private var _showsListLiveData = MutableLiveData<List<Show>>()
    val showsListLiveData: LiveData<List<Show>> = _showsListLiveData

    private var _currentUserLiveData = MutableLiveData<User>()
    val currentUserLiveData: LiveData<User> = _currentUserLiveData

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences


    fun setContext(context: Context, parentActivity: MainActivity){
        this.context = context
        loadShows()
        loadCurrentUser(parentActivity)

    }

    private fun loadCurrentUser(parentActivity: MainActivity) {
        sharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getCurrentUser(accessToken!!, client!!, uid!!).enqueue(object :
            Callback<CurrentUserResponse> {
            override fun onResponse(call: Call<CurrentUserResponse>, response: Response<CurrentUserResponse>) {
                if(response.isSuccessful){
                    _currentUserLiveData.value = response.body()!!.user
                } else {

                }
            }

            override fun onFailure(call: Call<CurrentUserResponse>, t: Throwable) {

            }


        })
    }

    private fun loadShows(){

        sharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getShows(accessToken!!, client!!, uid!!).enqueue(object :
            Callback<ShowsResponse> {
            override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                if(response.isSuccessful){
                    _showsListLiveData.value = response.body()!!.shows.asList()
                } else {

                }
            }

            override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {

            }


        })

   }


}