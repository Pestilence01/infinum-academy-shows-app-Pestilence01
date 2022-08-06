package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.app.Activity
import android.content.*
import android.content.SharedPreferences.Editor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.adapters.ShowsAdapter
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.responses.UpdateProfilePhotoResponse
import com.example.shows_lovre_nincevic_pestilence01.application.ShowsApplication
import com.example.shows_lovre_nincevic_pestilence01.database.modelfactory.ShowsViewModelFactory
import com.example.shows_lovre_nincevic_pestilence01.databinding.BottomSheetEditProfileBinding
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentShowsBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants
import com.example.shows_lovre_nincevic_pestilence01.viewmodels.ShowsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class ShowsFragment : Fragment(R.layout.fragment_shows) {



    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraIntent: ActivityResultLauncher<Intent>
    private lateinit var storageIntent: ActivityResultLauncher<Intent>

    private val viewModel: ShowsViewModel by viewModels {
        ShowsViewModelFactory((requireActivity().application as ShowsApplication).database)
    }


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ShowsAdapter
    private lateinit var username: String
    private lateinit var parentActivity: MainActivity
    private lateinit var bottomBinding: BottomSheetEditProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnResultListeners()

        parentActivity = (requireActivity() as MainActivity)


        viewModel.setContext(requireContext(), parentActivity)

        sharedPreferences =
            requireContext().getSharedPreferences(
                Constants.SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )

        username = sharedPreferences.getString(
            Constants.USERNAME_KEY,
            Constants.DEFAULT_USERNAME
        )!!


        val currentPhoto = getCurrentProfilePhoto()

        if (currentPhoto == null) {  // If the user hasn't changed their profile, it will be set to the default one
            Glide.with(requireContext()).load(R.drawable.ic_profile_placeholder).into(binding.editProfile)
        } else {
            Glide.with(requireContext()).load(currentPhoto).into(binding.editProfile)
        }

        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // this line hides the top of the screen (battery life, time, wifi...) allowing the application to take up the entire screen

        viewModel.showsListDatabaseLiveData.observe(viewLifecycleOwner) {
            if (!parentActivity.isOnline()){
                parentActivity.showProgressDialog()
                viewModel.loadShowsFromDB()
                parentActivity.hideProgressDialog()
            }

        }

        viewModel.showsListLiveData.observe(viewLifecycleOwner) {
            if (parentActivity.isOnline()) {


                if (it.isEmpty()) {
                    binding.showsRecyclerView.visibility = View.GONE
                    binding.constraintLayoutEmptyState.visibility = View.VISIBLE
                } else {
                    initShowsRecyclerView(it)
                }

            }
        }

        viewModel.loadShowsFromDB().observe(viewLifecycleOwner) {
            if (!parentActivity.isOnline()) {
                val showsList = mutableListOf<Show>()
                val iterator = it.iterator()
                while (iterator.hasNext()) {  // converts the list of ShowEntity to list of Show
                    val entity = iterator.next()
                    showsList.add(
                        Show(
                            id = entity.id,
                            average_rating = entity.average_rating,
                            description = entity.description,
                            image_url = entity.image_url,
                            no_of_reviews = entity.no_of_reviews,
                            title = entity.title
                        )
                    )
                }
                initShowsRecyclerView(showsList)
            }
        }


        setupEditProfileAndBottomSheet()

        binding.topRated.setOnClickListener {
            if (!parentActivity.isOnline()) {
                binding.topRated.isChecked = false
                parentActivity.showErrorSnackBar(Constants.PROVIDE_INTERNET, true)
            }

            if (binding.topRated.isChecked) {
                viewModel.loadTopRatedShows()
            } else {
                viewModel.loadShows()
            }
        }

    }

    private fun setupOnResultListeners() {
        cameraIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                val picture: Bitmap = result.data!!.extras!!.get("data") as Bitmap
                val filePath = bitmapToFile(picture, "${System.currentTimeMillis()}.png")
                updateProfilePicture(filePath!!)
                Glide.with(requireContext()).load(Uri.fromFile(filePath))
                    .into(bottomBinding.profilePicture)
                Glide.with(requireContext()).load(Uri.fromFile(filePath)).into(binding.editProfile)
            }
        }

        storageIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val pickedPhoto = result.data!!.data
                val source = ImageDecoder.createSource(requireContext().contentResolver, pickedPhoto!!)
                val bitmap = ImageDecoder.decodeBitmap(source)
                val filePath = bitmapToFile(bitmap, "${System.currentTimeMillis()}.png")
                updateProfilePicture(filePath!!)
                Glide.with(requireContext()).load(Uri.fromFile(filePath))
                    .into(bottomBinding.profilePicture)
                Glide.with(requireContext()).load(Uri.fromFile(filePath)).into(binding.editProfile)
            }
        }

    }


    private fun setupEditProfileAndBottomSheet() {

        binding.editProfile.setOnClickListener {
            createBottomSheet()
        }
    }

    private fun createBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(         // Created bottom sheet dialog
            requireContext(), R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutInflater.from(activity).inflate(
            R.layout.bottom_sheet_edit_profile, binding.root, false
            )


        bottomSheetDialog.setContentView(bottomSheetView.rootView)

        bottomBinding = BottomSheetEditProfileBinding.bind(bottomSheetView)


        val emailSharedPreferences =
            sharedPreferences.getString(Constants.EMAIL_KEY, Constants.DEFAULT_EMAIL)!!

        bottomBinding.emailAddressEditProfile!!.text = emailSharedPreferences

        Glide.with(requireContext()).load(R.drawable.ic_profile_placeholder)
            .into(bottomBinding.profilePicture)

        bottomSheetDialog.show()

        setupBottomSheetListeners(
            bottomBinding.logoutButton,
            bottomSheetDialog,
            bottomBinding.changeProfilePhoto
        )

    }

    private fun setupBottomSheetListeners(
        logout: Button?,
        bottomSheetDialog: BottomSheetDialog,
        changePhoto: Button?
    ) {
        logout?.setOnClickListener {
            createLogOutAlertDialog(bottomSheetDialog)

        }

        changePhoto?.setOnClickListener {
            createImageAlertDialog()
        }
    }

    private fun createImageAlertDialog() {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.AlertDialogTheme
        ).setTitle(Constants.IMAGE_SOURCE)
            .setPositiveButton(Constants.STORAGE, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    storageIntent.launch(intent)
                }
            }

            )
            .setNegativeButton(Constants.CAMERA, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.launch(intent)

                }

            }).show()
    }

    private fun createLogOutAlertDialog(bottomSheetDialog: BottomSheetDialog) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.AlertDialogTheme
        ).setTitle(Constants.LOG_OUT_PROMPT)
            .setPositiveButton(Constants.YES, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    parentActivity.showErrorSnackBar(
                        Constants.LOG_OUT_SUCCESSFUL,
                        false
                    )
                    bottomSheetDialog.dismiss()

                    val editor: Editor = sharedPreferences.edit()
                    editor.putBoolean(Constants.REMEMBER_ME_KEY, false)
                    editor.apply()

                    findNavController().navigate(R.id.action_showsFragment_to_loginFragment)
                    bottomSheetDialog.dismiss()
                }

            }).setNegativeButton(Constants.NO, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    bottomSheetDialog.dismiss()
                }

            }).show()

        bottomSheetDialog.dismiss()
    }


    private fun bitmapToFile(
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }


    private fun updateProfilePicture(filePath: File) {
        sharedPreferences =
            requireContext().getSharedPreferences(
                Constants.SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )

        val accessToken = sharedPreferences.getString("accessToken", "empty")
        val client = sharedPreferences.getString("client", "empty")
        val uid = sharedPreferences.getString("uid", "empty")


        val formData =
            MultipartBody.Part.createFormData("image", filePath.name, filePath.asRequestBody())

        val request =
            Request.Builder().header("access-token", accessToken!!).header("client", client!!)
                .header("uid", uid!!).url(Constants.AWS_URL).post(formData.body).build()

        ApiModule.initRetrofitAws(requireContext(), Constants.AWS_URL)

        ApiModule.retrofit.updateProfilePhoto(
            MultipartBody.Part.create(request.body!!)
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
                Log.i(
                    "FAILURE",
                    " ${t.cause}"
                )  // the error always says the connection to the server failed
            }


        })
    }

    private fun getCurrentProfilePhoto(): Bitmap? {  //returns the current profile photo or null (in that case the default placeholder is used)
        val bitmap: Bitmap
        try {

        } catch (e: Exception) {
            return null
        }
        return null
    }


    private fun initShowsRecyclerView(list: List<Show>) {
        adapter = ShowsAdapter(requireActivity(), list) { show ->
            val action = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(showId = show.id)
            findNavController().navigate(action)
        }
        binding.showsRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.showsRecyclerView.adapter = adapter
    }

}