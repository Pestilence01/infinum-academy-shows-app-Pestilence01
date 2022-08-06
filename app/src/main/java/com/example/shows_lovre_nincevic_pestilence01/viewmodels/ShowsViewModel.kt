package com.example.shows_lovre_nincevic_pestilence01.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.responses.CurrentUserResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.ShowsResponse
import com.example.shows_lovre_nincevic_pestilence01.database.ShowsDatabase
import com.example.shows_lovre_nincevic_pestilence01.database.entities.ShowEntity
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors


class ShowsViewModel(
    private val database: ShowsDatabase
): ViewModel() {

    private var _showsListLiveData = MutableLiveData<List<Show>>()
    val showsListLiveData: LiveData<List<Show>> = _showsListLiveData

    private var _showsListDatabaseLiveData = MutableLiveData<List<ShowEntity>>()
    val showsListDatabaseLiveData: LiveData<List<ShowEntity>> = _showsListDatabaseLiveData

    private var _currentUserLiveData = MutableLiveData<User>()
    val currentUserLiveData: LiveData<User> = _currentUserLiveData

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var parentActivity: MainActivity


    fun setContext(context: Context, parentActivity: MainActivity){
        this.context = context
        this.parentActivity = parentActivity
        if(parentActivity.isOnline()){
            parentActivity.showProgressDialog()
            loadShows()
            loadCurrentUser(parentActivity)
            parentActivity.hideProgressDialog()
        }


    }

    fun loadShowsFromDB(): LiveData<List<ShowEntity>> {
        return database.showDao().getAllShows()
    }

    private fun loadCurrentUser(parentActivity: MainActivity) {

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getCurrentUser().enqueue(object :
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

    fun loadShows(){

        ApiModule.initRetrofit(context)

        ApiModule.retrofit.getShows().enqueue(object :
            Callback<ShowsResponse> {
            override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                if(response.isSuccessful){
                    _showsListLiveData.value = response.body()!!.shows.asList()
                    Executors.newSingleThreadExecutor().execute{
                        insertShowsToDB(_showsListLiveData.value!!)
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {

            }


        })

   }

    private fun insertShowsToDB(shows: List<Show>) {
        val entities = mutableListOf<ShowEntity>()
        val iterator = shows.iterator()
        while(iterator.hasNext()){
            val show = iterator.next()
            val entity = ShowEntity(id = show.id, average_rating = show.average_rating, description = show.description, image_url = show.image_url, no_of_reviews = show.no_of_reviews, title = show.title)
            entities.add(entity)
        }
        database.showDao().insertAllShows(entities)
    }

    fun loadTopRatedShows() {

        ApiModule.initRetrofit(context)


        ApiModule.retrofit.getTopRatedShows().enqueue(object :
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