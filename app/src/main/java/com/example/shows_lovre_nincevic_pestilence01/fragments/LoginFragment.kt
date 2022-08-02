package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.animation.Animator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.BounceInterpolator
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.requests.LoginRequest
import com.example.shows_lovre_nincevic_pestilence01.api.responses.LoginResponse
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentLoginBinding
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(R.layout.fragment_login) {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var username: String

    private lateinit var parentActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

    }

    override fun onResume() { //checks if the user just registered
        super.onResume()
        val justRegistered = sharedPreferences.getBoolean(Constants.REGISTERED_KEY, false)
        if (justRegistered) {
            binding.registerButton.visibility = View.GONE
            binding.login.text = Constants.REGISTRATION_SUCCESSFUL_NEWLINE
        } else {
            binding.registerButton.visibility = View.VISIBLE
            binding.login.text = Constants.LOGIN
        }

        sharedPreferences.edit().apply() {
            putBoolean(Constants.REGISTERED_KEY, false)
        }.apply()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showsTitle.visibility = View.GONE

        animateTriangle()

        parentActivity = (activity!! as MainActivity)

        ApiModule.initRetrofit(requireContext())

        val savedBoolean = sharedPreferences.getBoolean(Constants.REMEMBER_ME_KEY, false)


        if (savedBoolean) {
            findNavController().navigate(R.id.action_loginFragment_to_showsFragment)
        }

        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.emailLoginText.doOnTextChanged { _, _, _, _ ->
            toggleButtonEnabled()
        }

        binding.passwordLoginText.doOnTextChanged { _, _, _, _ ->
            toggleButtonEnabled()
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginButton.setOnClickListener {


            val emailPattern: Regex =
                Regex(".+@.+[.].*") // pattern ensures that there is at least one character before "@" and at least one character after "@", the "@" is always present. Updated to account for the domain

            if (emailPattern.containsMatchIn(binding.emailLoginText.text.toString())) {

                username =
                    binding.emailLoginText.text!!.split("@")[0]   // Username is the string before "@"

                sharedPreferences.edit().apply() {
                    putBoolean(Constants.REMEMBER_ME_KEY, binding.rememberMe.isChecked)
                    putString(Constants.USERNAME_KEY, username)
                    putString(Constants.EMAIL_KEY, binding.emailLoginText.text.toString())
                }.apply()

                parentActivity.showProgressDialog()

                val loginRequest = LoginRequest(
                    email = binding.emailLoginText.text.toString(),
                    password = binding.passwordLoginText.text.toString()
                )
                ApiModule.retrofit.loginUser(loginRequest).enqueue(object :
                    Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val accessToken = response.headers().get("access-token")
                            val client = response.headers().get("client")
                            val uid = response.headers().get("uid")

                            sharedPreferences.edit().apply() {
                                putString("accessToken", accessToken)
                                putString("client", client)
                                putString("uid", uid)
                            }.apply()
                            parentActivity.hideProgressDialog()
                            findNavController().navigate(R.id.action_loginFragment_to_showsFragment)
                            parentActivity.showErrorSnackBar(
                                Constants.SUCCESSFUL_LOGIN,
                                false
                            )
                        } else {
                            parentActivity.showErrorSnackBar(
                                Constants.VALID_CREDENTIALS,
                                true
                            )
                            parentActivity.hideProgressDialog()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        parentActivity.showErrorSnackBar(Constants.CHECK_CONNECTION, true)
                        parentActivity.hideProgressDialog()
                    }


                })

            } else {
                binding.emailLoginText.setError(Constants.VALID_EMAIL)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun animateTitle() = with(binding.showsTitle){  // this animation firstly scales the title to 0, then up-scales it by 2, then the end action makes it return to its original 1f size
        scaleX = 0f
        scaleY = 0f
        animate().scaleX(1.3f).scaleY(1.3f).setDuration(350).withEndAction(Runnable {
            animate().scaleX(1f).scaleY(1f).setDuration(200)
        })
    }

    private fun animateTriangle() = with(binding.triangleLogin){
        translationY = -500f  // starts from 500 units up the Y axis then falls to 0 (ending position)
        animate().translationY(0f).setDuration(1300).setInterpolator(BounceInterpolator()).setListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    binding.showsTitle.visibility = View.VISIBLE
                    animateTitle()  // when this animation ends, the second one can start. No need to delay the thread or use the sleep function
                }

                override fun onAnimationCancel(p0: Animator?) {
                    TODO("Not yet implemented")
                }

                override fun onAnimationRepeat(p0: Animator?) {
                    TODO("Not yet implemented")
                }

            })
    }


    private fun toggleButtonEnabled() {

        if (binding.emailLoginText.text.toString()
                .isBlank() || binding.passwordLoginText.text.toString().length < 6
        )
            binding.loginButton.setEnabled(false)
        else binding.loginButton.setEnabled(true)
    }

}