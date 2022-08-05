package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.adapters.ReviewsAdapter
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentShowDetailsBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Review
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants
import com.example.shows_lovre_nincevic_pestilence01.utils.ImageSaver
import com.example.shows_lovre_nincevic_pestilence01.viewmodels.ShowDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception


class ShowDetailsFragment : Fragment(R.layout.fragment_show_details) {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ShowDetailsViewModel>()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ReviewsAdapter
    private lateinit var username: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()  // Sets up the action bar

        sharedPreferences = requireContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        username = sharedPreferences.getString(Constants.USERNAME_KEY, "John Doe").toString()

        viewModel.setShow(arguments!!.get(Constants.SHOW_EXTRA_KEY) as Show)

        viewModel.showLiveData.observe(viewLifecycleOwner){
            adjustUI(it.reviews)

            if(viewModel.checkIfReviewPosted(username)){
                toggleReviewButtonOff()
            }


            val averageReview = viewModel.calculateAverageReview()
            updateRatingUI(averageReview, it.reviews.size)

            initReviewsRecyclerView(it.reviews)
            adjustActionBarToScroll()
            instantiateBottomSheet(it.reviews)
        }

    }

    private fun updateRatingUI(result: String, amountOfReviews: Int) {
        binding.averageReview.text = "$amountOfReviews REVIEWS, $result AVERAGE"
        binding.ratingBar.rating = result.toFloat()
    }

    private fun instantiateBottomSheet(reviewList: ArrayList<Review>) {
        binding.addReviewButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(         // Created bottom sheet dialog
                activity!!, R.style.BottomSheetDialogTheme
            )

            val bottomSheetView = LayoutInflater.from(activity).inflate(
                R.layout.bottom_sheet_review_layout, activity!!.findViewById(
                    R.id.bottomSheet
                ))

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

            val submit: Button? = bottomSheetDialog.findViewById<Button>(R.id.submitButton)      // I couldn't find a way to bind the elements from the dialog so I used the old fashioned findViewById way. If you, reader, know how to fix this problem, I would appreciate it.
            val rating: RatingBar? = bottomSheetDialog.findViewById<RatingBar>(R.id.ratingBar)
            val review: TextInputEditText? = bottomSheetDialog.findViewById(R.id.review)
            val cancel: ImageView? = bottomSheetDialog.findViewById(R.id.dismissBottomSheet)

            cancel!!.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            submit?.setOnClickListener {
                var bitmap: Bitmap?
                try{
                    bitmap = ImageSaver(activity!!).setFileName("${username}.png").setDirectoryName("images").load()!!
                } catch (e: Exception){
                    bitmap = null
                }
                val newReview = Review(review?.text.toString(), username, rating!!.rating.toInt(), bitmap)
                reviewList.add(newReview)
                adapter.notifyItemInserted(reviewList.size - 1)
                if(reviewList.size == 1){   // If this is the first review, it adjusts the UI
                    binding.layoutReviews.visibility = View.VISIBLE
                    binding.noReviews.visibility = View.GONE
                }
                val averageReview = viewModel.calculateAverageReview()
                updateRatingUI(averageReview, reviewList.size)
                toggleReviewButtonOff()
                Toast.makeText(activity, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            }


        }
    }

    private fun adjustUI(reviewList: ArrayList<Review>) {
        if(reviewList.isEmpty()){
            binding.layoutReviews.visibility = View.GONE
            binding.noReviews.visibility = View.VISIBLE
        } else {
            binding.layoutReviews.visibility = View.VISIBLE
            binding.noReviews.visibility = View.GONE
        }

        binding.showTitleActionBar.text = viewModel.showLiveData.value!!.title
        binding.showTitle.text = viewModel.showLiveData.value!!.title
        binding.showDescription.text = viewModel.showLiveData.value!!.description
        binding.showPicture.setImageResource(viewModel.showLiveData.value!!.imageResourceID)

    }

    private fun adjustActionBarToScroll() {
        binding.mainScreenScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->      //Changes the ActionBar when scrolled. It looks janky, but I am sure a custom animation could fix it. I will look into this in the future.
            if(scrollY > 0){
                (activity as AppCompatActivity).supportActionBar?.elevation = 10f
                (activity as AppCompatActivity).supportActionBar?.title = ""
                binding.showTitleActionBar.visibility = View.VISIBLE
                binding.showTitle.visibility = View.GONE
            }
            else{
                (activity as AppCompatActivity).supportActionBar?.elevation = 0f
                binding.showTitleActionBar.visibility = View.GONE
                binding.showTitle.visibility = View.VISIBLE
            }

        }
    }


    private fun toggleReviewButtonOff() {
        binding.addReviewButton.setEnabled(false)
        binding.addReviewButton.text = "Already added a review"
    }



    private fun setupActionBar() {


        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarShowDetailsActivity)    // the cast to AppCompat is necessary to access the setSupportActionBar method

        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.elevation = 5f   // Adds a divider between action bar and main screen
        }

        binding.toolbarShowDetailsActivity.setNavigationOnClickListener { activity!!.onBackPressed() }
    }

    private fun initReviewsRecyclerView(reviewList: ArrayList<Review>) {
        adapter = ReviewsAdapter(reviewList, context!!)

        binding.reviews.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.reviews.adapter = adapter
        binding.reviews.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }
}