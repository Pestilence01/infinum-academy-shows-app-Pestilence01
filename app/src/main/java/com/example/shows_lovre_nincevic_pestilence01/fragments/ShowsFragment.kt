package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.app.Activity
import android.content.*
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.adapters.ShowsAdapter
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.responses.CurrentUserResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.PostReviewResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.ShowsResponse
import com.example.shows_lovre_nincevic_pestilence01.api.responses.UpdateProfilePhotoResponse
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentShowsBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.models.User
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants
import com.example.shows_lovre_nincevic_pestilence01.utils.ImageSaver
import com.example.shows_lovre_nincevic_pestilence01.viewmodels.ShowsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class ShowsFragment : Fragment(R.layout.fragment_shows) {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2

        private const val STORAGE_PERMISSION_CODE = 3
        private const val STORAGE_REQUEST_CODE = 4
    }


    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ShowsViewModel>()


    private lateinit var photo: CircleImageView


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ShowsAdapter
    private lateinit var username: String
    private lateinit var parentActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = (activity!! as MainActivity)


        viewModel.setContext(requireContext(), parentActivity)

        sharedPreferences =
            requireContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

        username = sharedPreferences.getString(Constants.USERNAME_KEY, "John Doe")!!


        val currentPhoto = getCurrentProfilePhoto()

        if (currentPhoto == null) {  // If the user hasn't changed their profile, it will be set to the default one
            Glide.with(context!!).load(R.drawable.ic_profile_placeholder).into(binding.editProfile)
        } else {
            Glide.with(context!!).load(currentPhoto).into(binding.editProfile)
        }

        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // this line hides the top of the screen (battery life, time, wifi...) allowing the application to take up the entire screen

        viewModel.showsListLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.showsRecyclerView.visibility = View.GONE
                binding.constraintLayoutEmptyState.visibility = View.VISIBLE
                parentActivity.hideProgressDialog()
            } else {
                initShowsRecyclerView(it)
                parentActivity.hideProgressDialog()
            }
        }


        setupEditProfileAndBottomSheet()

        binding.topRated.setOnClickListener {
            if (binding.topRated.isChecked) {
                viewModel.loadTopRatedShows()
            } else {
                viewModel.loadShows()
            }
        }

    }


    private fun setupEditProfileAndBottomSheet() {

        binding.editProfile.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(         // Created bottom sheet dialog
                activity!!, R.style.BottomSheetDialogTheme
            )

            val bottomSheetView = LayoutInflater.from(activity).inflate(
                R.layout.bottom_sheet_edit_profile, activity!!.findViewById(
                    R.id.bottomSheet
                )
            )

            bottomSheetDialog.setContentView(bottomSheetView)

            val currentPhoto = getCurrentProfilePhoto()


            photo =
                bottomSheetDialog.findViewById<CircleImageView>(R.id.profilePicture)!!      // I couldn't find a way to bind the elements from the dialog so I used the old fashioned findViewById way. If you, reader, know how to fix this problem, I would appreciate it.
            val email: TextView? =
                bottomSheetDialog.findViewById<TextView>(R.id.emailAddressEditProfile)
            val changePhoto: Button? = bottomSheetDialog.findViewById(R.id.changeProfilePhoto)
            val logout: Button? = bottomSheetDialog.findViewById(R.id.logoutButton)

            val emailSharedPreferences =
                sharedPreferences.getString(Constants.EMAIL_KEY, "JohnDoe@gmail.com")!!

            email!!.text = emailSharedPreferences

            if (currentPhoto == null) {
                Glide.with(context!!).load(R.drawable.ic_profile_placeholder).into(photo)
            } else {
                Glide.with(context!!).load(currentPhoto).into(photo)
            }

            bottomSheetDialog.show()

            logout?.setOnClickListener {

                MaterialAlertDialogBuilder(
                    context!!,
                    R.style.AlertDialogTheme
                ).setTitle("Are you sure you want to log out?")
                    .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            parentActivity.showErrorSnackBar(
                                "You have successfully logged out!",
                                false
                            )
                            bottomSheetDialog.dismiss()

                            val editor: Editor = sharedPreferences.edit()
                            editor.putBoolean(Constants.REMEMBER_ME_KEY, false)
                            editor.apply()

                            findNavController().navigate(R.id.action_showsFragment_to_loginFragment)
                            bottomSheetDialog.dismiss()
                        }

                    }).setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        bottomSheetDialog.dismiss()
                    }

                }).show()

                bottomSheetDialog.dismiss()
            }

            changePhoto?.setOnClickListener {

                MaterialAlertDialogBuilder(
                    context!!,
                    R.style.AlertDialogTheme
                ).setTitle("Where do you want to take your image from?")
                    .setPositiveButton("Storage", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            if (ContextCompat.checkSelfPermission(
                                    context!!,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                val intent = Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                                startActivityForResult(intent, STORAGE_REQUEST_CODE)
                            } else {
                                ActivityCompat.requestPermissions(
                                    activity!!,
                                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                                    STORAGE_PERMISSION_CODE
                                )
                            }
                        }

                    }).setNegativeButton("Camera", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        if (ContextCompat.checkSelfPermission(
                                context!!,
                                android.Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA_REQUEST_CODE)
                        } else {
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf(android.Manifest.permission.CAMERA),
                                CAMERA_PERMISSION_CODE
                            )
                        }
                    }

                }).show()
            }

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(
                    context!!,
                    "Please enable the permissions for this feature in the settings!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, STORAGE_REQUEST_CODE)
            } else {
                Toast.makeText(
                    context!!,
                    "Please enable the permissions for this feature in the settings!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                val picture: Bitmap = data!!.extras!!.get("data") as Bitmap
                val imageSaver =
                    ImageSaver(activity!!).setFileName("${username}.png").setDirectoryName("images")
                        .save(picture)
                updateProfilePicture()
                Glide.with(context!!).load(picture).into(photo)
                Glide.with(context!!).load(picture).into(binding.editProfile)
            }
            if (requestCode == STORAGE_REQUEST_CODE) {
                val pickedPhoto = data!!.data
                Log.i("PATH: ", pickedPhoto!!.path.toString())
                val source = ImageDecoder.createSource(activity!!.contentResolver, pickedPhoto!!)
                val bitmap = ImageDecoder.decodeBitmap(source)
                val imageSaver =
                    ImageSaver(activity!!).setFileName("${username}.png").setDirectoryName("images")
                        .save(bitmap)
                updateProfilePicture()
                Glide.with(context!!).load(pickedPhoto).into(photo)
                Glide.with(context!!).load(pickedPhoto).into(binding.editProfile)
            }
        }
    }

    private fun updateProfilePicture() {    // unsuccessful attempt to update the profile photo on AWS :)
        sharedPreferences =
            requireContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")

        ApiModule.initRetrofitAws(requireContext(), Constants.AWS_URL)

        ApiModule.retrofit.updateProfilePhoto(
            accessToken!!,
            client!!,
            uid!!,
            "/images/${username}.png"
        ).enqueue(object :
            Callback<UpdateProfilePhotoResponse> {
            override fun onResponse(
                call: Call<UpdateProfilePhotoResponse>,
                response: Response<UpdateProfilePhotoResponse>
            ) {
                if (response.isSuccessful) {
                    Log.i("IS SUCCESSFUL UPDATE PHOTO", " YES")
                } else {
                    Log.i("IS SUCCESSFUL", " NO")
                }
            }

            override fun onFailure(call: Call<UpdateProfilePhotoResponse>, t: Throwable) {
                Log.i("FAILURE", " YES")
            }


        })
    }

    private fun getCurrentProfilePhoto(): Bitmap? {  //returns the current profile photo or null (in that case the default placeholder is used)
        val bitmap: Bitmap
        try {
            bitmap =
                ImageSaver(activity!!).setFileName("${username}.png").setDirectoryName("images")
                    .load()!!
        } catch (e: Exception) {
            return null
        }
        return bitmap
    }


    private fun initShowsRecyclerView(list: List<Show>) {
        adapter = ShowsAdapter(activity!!, list)

        binding.showsRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.showsRecyclerView.adapter = adapter
    }

}