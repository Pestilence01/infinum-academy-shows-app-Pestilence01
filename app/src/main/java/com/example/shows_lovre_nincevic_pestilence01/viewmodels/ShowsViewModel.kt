package com.example.shows_lovre_nincevic_pestilence01.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.models.Review
import com.example.shows_lovre_nincevic_pestilence01.models.Show


class ShowsViewModel: ViewModel() {

    private var _showsListLiveData = MutableLiveData<List<Show>>()
    val showsListLiveData: LiveData<List<Show>> = _showsListLiveData

    init {
        _showsListLiveData.value = loadShows()
    }

    private fun loadShows(): List<Show> {   // Returns hard coded shows



        return listOf(
            Show(
                "id_office",
                "The Office",
                "The Office is an American mockumentary sitcom television series that depicts the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons.",
                R.drawable.the_office,
                ArrayList<Review>(loadReviews("office"))
            ),
            Show(
                "id_stranger_things",
                "Stranger Things",
                "In a small town where everyone knows everyone, a peculiar incident starts a chain of events that leads to a child's disappearance, which begins to tear at the fabric of an otherwise-peaceful community. Dark government agencies and seemingly malevolent supernatural forces converge on the town, while a few of the locals begin to understand that more is going on than meets the eye.",
                R.drawable.ic_stranger_things,
                ArrayList<Review>(loadReviews("stranger_things"))
            ),
            Show(
                "id_krv_nije_voda",
                "Krv Nije Voda",
                "Worst show in the history of Croatian television!",
                R.drawable.krv_nije_voda,
                ArrayList<Review>(loadReviews("krv_nije_voda"))
            )
        )
    }

    private fun loadReviews(showID : String): List<Review> {  // Returns hard coded custom comments for every show

        when (showID) {

            "office" -> return listOf(
                Review("These are the droids we are looking for!","Obi Wan", 5, null),
                Review("MHM excellent it is! But resist the dark side fully it does not!","Yoda", 4, null),
                Review("Nothing compared to the power of the dark side!","Darth Vader", 1, null)
            )

            "stranger_things" -> return listOf() //empty list

            "krv_nije_voda" ->  return listOf(
                Review("Star wars is better!","Princess Leia", 2, null),
                Review("","Jar Jar", 2, null),
                Review("And they said I was a bad actor...","Anakin", 1, null)
            )
            else -> return listOf()

        }

    }
}