package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.adapters.ReviewsAdapter
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.requests.PostReviewRequest
import com.example.shows_lovre_nincevic_pestilence01.api.responses.PostReviewResponse
import com.example.shows_lovre_nincevic_pestilence01.application.ShowsApplication
import com.example.shows_lovre_nincevic_pestilence01.database.modelfactory.ShowDetailsViewModelFactory
import com.example.shows_lovre_nincevic_pestilence01.databinding.BottomSheetReviewLayoutBinding
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentShowDetailsBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Review
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants
import com.example.shows_lovre_nincevic_pestilence01.viewmodels.ShowDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShowDetailsFragment : Fragment(R.layout.fragment_show_details) {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowDetailsViewModel by viewModels{
        ShowDetailsViewModelFactory((requireActivity().application as ShowsApplication).database)
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ReviewsAdapter
    private lateinit var username: String
    private lateinit var parentActivity: MainActivity
    private lateinit var showId: String

    private lateinit var bottomBinding: BottomSheetReviewLayoutBinding

    private val args: ShowDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = (requireActivity() as MainActivity)

        showId = args.showId

        setupActionBar()  // Sets up the action bar

        sharedPreferences =
            requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        username = sharedPreferences.getString(Constants.USERNAME_KEY, Constants.DEFAULT_USERNAME).toString()

        viewModel.setParameters(
            requireContext(),
            showId,
            parentActivity
        )

        viewModel.showLiveData.observe(viewLifecycleOwner) {
            if(parentActivity.isOnline()){
                setupShowUI()
            }
        }

        viewModel.getReviewsFromDB(showId).observe(viewLifecycleOwner){
            if(!parentActivity.isOnline()){
                parentActivity.showProgressDialog()
                val showList = mutableListOf<Review>()
                val iterator = it.iterator()
                while(iterator.hasNext()){
                    val entity = iterator.next()
                    showList.add(Review(id = entity.id, comment = entity.comment, rating = entity.rating, show_id = entity.id, user = entity.user))
                }
                viewModel.setReviews(showList)
                parentActivity.hideProgressDialog()
            }
        }

        viewModel.getShowFromDB(showId).observe(viewLifecycleOwner){
            if(!parentActivity.isOnline()){
                parentActivity.showProgressDialog()
                viewModel.setShow(Show(id = it.id, average_rating = it.average_rating, description = it.description, image_url = it.image_url, no_of_reviews = it.no_of_reviews, title = it.title))
                setupShowUI()
                parentActivity.hideProgressDialog()
            }
        }

        viewModel.reviewsLiveData.observe(viewLifecycleOwner) {
            initReviewsRecyclerView()
            instantiateBottomSheet()
        }


    }

    private fun setupShowUI() {
        binding.showTitleActionBar.text = viewModel.showLiveData.value!!.title
        binding.showTitle.text = viewModel.showLiveData.value!!.title
        Glide.with(requireContext()).load(viewModel.showLiveData.value!!.image_url)
            .into(binding.showPicture)
        binding.showDescription.text = viewModel.showLiveData.value!!.description
        checkReviewAmount()
        setupRatingDetails()
    }

    private fun setupRatingDetails() {
        binding.averageReview.text = getString(R.string.review_details, viewModel.showLiveData.value!!.no_of_reviews, viewModel.showLiveData.value!!.average_rating)
        binding.ratingBar.rating = viewModel.showLiveData.value!!.average_rating!!.toFloat()
    }

    private fun checkReviewAmount() {
        if (viewModel.showLiveData.value!!.no_of_reviews == "0") {
            binding.layoutReviews.visibility = View.GONE
            binding.noReviews.visibility = View.VISIBLE
        } else {
            binding.layoutReviews.visibility = View.VISIBLE
            binding.noReviews.visibility = View.GONE
        }
    }


    private fun instantiateBottomSheet() {
        binding.addReviewButton.setOnClickListener {          // if there is no internet, the review cannot be posted. It can be stored to the database but then the moment the connection returns it needs to be uploaded to the API. Let me know if this is something you want me to implement
            if (!parentActivity.isOnline()) {
                parentActivity.showErrorSnackBar(Constants.PROVIDE_INTERNET, true)
            } else {
                val bottomSheetDialog = BottomSheetDialog(         // Created bottom sheet dialog
                    requireContext(), R.style.BottomSheetDialogTheme
                )

                val bottomSheetView = LayoutInflater.from(activity).inflate(
                    R.layout.bottom_sheet_review_layout, binding.root, false
                )

                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()

                bottomBinding = BottomSheetReviewLayoutBinding.bind(bottomSheetView)

                bottomBinding.dismissBottomSheet.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }

                bottomBinding.submitButton.setOnClickListener {
                    postReview()
                    adapter.notifyDataSetChanged()  // I do not know how to make it update in real-time. It doesn't post the review but if I leave the fragment and come back to it, it's there. Any tips on how to update it in real-time?
                    bottomSheetDialog.dismiss()
                }


            }
        }
    }

    private fun postReview() {
        parentActivity.showProgressDialog()
        val request = PostReviewRequest(
            bottomBinding.ratingBar.rating.toDouble().toInt().toString(),
            bottomBinding.review.text.toString(),
            viewModel.showLiveData.value!!.id
        )

        ApiModule.initRetrofit(requireContext())

        ApiModule.retrofit.postReviews(request).enqueue(object :
            Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                if (response.isSuccessful) {
                    parentActivity.showErrorSnackBar(Constants.FEEDBACK, false)
                    parentActivity.hideProgressDialog()
                } else {
                    parentActivity.showErrorSnackBar(
                        Constants.REVIEW_PROBLEM,
                        true
                    )
                    parentActivity.hideProgressDialog()

                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                parentActivity.showErrorSnackBar(Constants.SOMETHING_WRONG, true)
                parentActivity.hideProgressDialog()

            }


        })
    }


    private fun adjustActionBarToScroll() {
        binding.mainScreenScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                (activity as AppCompatActivity).supportActionBar?.elevation = 10f
                (activity as AppCompatActivity).supportActionBar?.title = ""
                binding.showTitleActionBar.visibility = View.VISIBLE
                binding.showTitle.visibility = View.GONE
            } else {
                (activity as AppCompatActivity).supportActionBar?.elevation = 0f
                binding.showTitleActionBar.visibility = View.GONE
                binding.showTitle.visibility = View.VISIBLE
            }

        }
    }


    private fun setupActionBar() {


        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarShowDetailsActivity)    // the cast to AppCompat is necessary to access the setSupportActionBar method

        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.elevation = 5f   // Adds a divider between action bar and main screen
        }

        binding.toolbarShowDetailsActivity.setNavigationOnClickListener { requireActivity().onBackPressed() }

        adjustActionBarToScroll()
    }

    private fun initReviewsRecyclerView() {
        if(viewModel.reviewsLiveData.value!!.isEmpty()){
            binding.noReviews.visibility = View.VISIBLE
            binding.layoutReviews.visibility = View.GONE

        } else {

            binding.noReviews.visibility = View.GONE
            binding.layoutReviews.visibility = View.VISIBLE

            adapter = ReviewsAdapter(viewModel.reviewsLiveData.value!!, requireContext())

            binding.reviews.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.reviews.adapter = adapter
            binding.reviews.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }
}