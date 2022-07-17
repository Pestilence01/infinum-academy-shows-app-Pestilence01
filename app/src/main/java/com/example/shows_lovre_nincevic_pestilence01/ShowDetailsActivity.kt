package com.example.shows_lovre_nincevic_pestilence01


import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityShowDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.math.RoundingMode
import java.text.DecimalFormat


class ShowDetailsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var adapter: ReviewsAdapter
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var show: Show
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.hasExtra(Constants.SHOW_EXTRA_KEY)){
            show = intent.getParcelableExtra<Show>(Constants.SHOW_EXTRA_KEY)!!
        }

        if(intent.hasExtra(Constants.LOGIN_EMAIL_KEY)){
            username = intent.getStringExtra(Constants.LOGIN_EMAIL_KEY)!!
        }


        reviewList = show.reviews

        if(reviewList.isEmpty()){
            binding.layoutReviews.visibility = View.GONE
            binding.noReviews.visibility = View.VISIBLE
        }

        binding.showTitleActionBar.text = show.title
        binding.showTitle.text = show.title
        binding.showDescription.text = show.description
        binding.showPicture.setImageResource(show.imageResourceID)

        updateAverageReview()  // Sets up the review scores and adjusts them when a new review is posted

        if(checkIfReviewPosted()){
            toggleReviewButtonOff()
        }

        setupActionBar()  // Sets up the action bad

        initReviewsRecyclerView()

        binding.mainScreenScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->      //Changes the ActionBar when scrolled. It looks janky, but I am sure a custom animation could fix it. I will look into this in the future.
            if(scrollY > 0){
                supportActionBar?.elevation = 10f
                supportActionBar?.title = ""
                binding.showTitleActionBar.visibility = View.VISIBLE
                binding.showTitle.visibility = View.GONE
            }
            else{
                supportActionBar?.elevation = 0f
                binding.showTitleActionBar.visibility = View.GONE
                binding.showTitle.visibility = View.VISIBLE
            }

        }

        binding.addReviewButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(         // Created bottom sheet dialog
                this, R.style.BottomSheetDialogTheme
            )

            val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_review_layout, findViewById(R.id.bottomSheet))


            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

            val submit: Button? = bottomSheetDialog.findViewById<Button>(R.id.submitButton)      // I couldn't find a way to bind the elements from the dialog so I used the old fashioned findViewById way. If you, reader, know how to fix this problem, I would appreciate it.
            val rating: RatingBar? = bottomSheetDialog.findViewById<RatingBar>(R.id.ratingBar)
            val review: TextInputEditText? = bottomSheetDialog.findViewById(R.id.review)

            submit?.setOnClickListener {
                val newReview = Review(review?.text.toString(), username, rating!!.rating.toInt(), R.drawable.ic_profile_placeholder)
                reviewList.add(newReview)
                adapter.notifyItemInserted(reviewList.size - 1)
                if(reviewList.size == 1){   // If this is the first review, it adjusts the UI
                    binding.layoutReviews.visibility = View.VISIBLE
                    binding.noReviews.visibility = View.GONE
                }
                updateAverageReview()
                toggleReviewButtonOff()
                Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            }


        }

    }

    private fun checkIfReviewPosted(): Boolean {

        for(i in reviewList.indices){
            if(username == reviewList[i].username)
                return true
        }

        return false
    }

    private fun toggleReviewButtonOff() {
        binding.addReviewButton.setEnabled(false)
        binding.addReviewButton.text = "Already added a review"
    }


    private fun updateAverageReview() {
        var amountOfReviews: Int = 0
        var sum: Int = 0

        for(i in reviewList.indices){
            amountOfReviews++
            sum += reviewList[i].rating
        }

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val result = df.format(sum.toDouble() / amountOfReviews)

        binding.averageReview.text = "$amountOfReviews REVIEWS, $result AVERAGE"
        binding.ratingBar.rating = result.toFloat()
    }

     private fun setupActionBar() {

        setSupportActionBar(binding.toolbarShowDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.elevation = 5f   // Adds a divider between action bar and main screen
        }

        binding.toolbarShowDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initReviewsRecyclerView() {
        adapter = ReviewsAdapter(reviewList)

        binding.reviews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.reviews.adapter = adapter
        binding.reviews.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}