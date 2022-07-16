package com.example.shows_lovre_nincevic_pestilence01


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityShowDetailsBinding
import com.example.shows_lovre_nincevic_pestilence01.databinding.BottomSheetReviewLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.math.RoundingMode
import java.text.DecimalFormat


class ShowDetailsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var bottomSheetBinding: BottomSheetReviewLayoutBinding
    private lateinit var adapter: ReviewsAdapter
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var show: Show
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.hasExtra("Show")){
            show = intent.getParcelableExtra<Show>("Show")!!
        }

        if(intent.hasExtra(Constants.LOGIN_EMAIL_KEY)){
            username = intent.getStringExtra(Constants.LOGIN_EMAIL_KEY)!!
        }


        reviewList = show.reviews

        if(reviewList.isEmpty()){
            binding.layoutReviews.visibility = View.GONE
            binding.noReviews.visibility = View.VISIBLE
        }

        binding.tvTitle.text = show.title
        binding.showDescription.text = show.description
        binding.showPicture.setImageResource(show.imageResourceID)

        updateAverageReview()

        if(checkIfReviewPosted()){
            toggleReviewButtonOff()
        }

        setupActionBar()

        initReviewsRecyclerView()

        binding.addReviewButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
            )

            val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_review_layout, findViewById(R.id.bottomSheet))


            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

            val submit: Button? = bottomSheetDialog.findViewById<Button>(R.id.submitButton)
            val rating: RatingBar? = bottomSheetDialog.findViewById<RatingBar>(R.id.ratingBar)
            val review: TextInputEditText? = bottomSheetDialog.findViewById(R.id.review)

            submit?.setOnClickListener {
                val newReview = Review(review?.text.toString(), username, rating!!.rating.toInt(), R.drawable.ic_profile_placeholder)
                reviewList.add(newReview)
                adapter.notifyItemInserted(reviewList.size - 1)
                if(reviewList.size == 1){
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
    }

     private fun setupActionBar() {

        setSupportActionBar(binding.toolbarShowDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
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