package com.example.shows_lovre_nincevic_pestilence01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityShowsBinding

class ShowsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowsBinding
    private lateinit var adapter: ShowsAdapter
    private lateinit var showsList: List<Show>
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shows)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // this line hides the top of the screen (battery life, time, wifi...) allowing the application to take up the entire screen

        if(intent.hasExtra(Constants.LOGIN_EMAIL_KEY)){
            username = intent.getStringExtra(Constants.LOGIN_EMAIL_KEY)!!
        }


        binding = ActivityShowsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showsList = loadShows()

        // showsList = listOf()     --> uncomment this line to show EmptyState

        if(showsList.isEmpty()){
            binding.showsRecyclerView.visibility = View.GONE
            binding.constraintLayoutEmptyState.visibility = View.VISIBLE
        }

        else{
            initShowsRecyclerView()
        }
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
                Review("These are the droids we are looking for!","Obi Wan", 5, R.drawable.ic_profile_placeholder),
                Review("MHM excellent it is! But resist the dark side fully it does not!","Yoda", 4, R.drawable.ic_profile_placeholder),
                Review("Nothing compared to the power of the dark side!","Darth Vader", 1, R.drawable.ic_profile_placeholder)
            )

            "stranger_things" -> return emptyList() //empty list

            "krv_nije_voda" ->  return listOf(
                Review("Star wars is better!","Princess Leia", 2, R.drawable.ic_profile_placeholder),
                Review("","Jar Jar", 2, R.drawable.ic_profile_placeholder),
                Review("And they said I was a bad actor...","Anakin", 1, R.drawable.ic_profile_placeholder)
            )
            else -> return listOf()

            }

    }

    private fun initShowsRecyclerView() {
        adapter = ShowsAdapter(this, username, showsList)

        binding.showsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.showsRecyclerView.adapter = adapter
    }
}