package com.example.shows_lovre_nincevic_pestilence01.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import java.math.RoundingMode
import java.text.DecimalFormat

class ShowDetailsViewModel: ViewModel() {

    private var _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData


    fun setShow(show: Show){
        _showLiveData.value = show
    }

    fun calculateAverageReview(): String {
        var amountOfReviews: Int = 0
        var sum: Int = 0

        val reviewList = showLiveData.value!!.reviews

        for (i in reviewList.indices) {
            amountOfReviews++
            sum += reviewList[i].rating
        }

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN

        return df.format(sum.toDouble() / amountOfReviews)

    }

    fun checkIfReviewPosted(username: String): Boolean {

        val reviewList = showLiveData.value!!.reviews

        for(i in reviewList.indices){
            if(username == reviewList[i].username)
                return true
        }

        return false
    }

}