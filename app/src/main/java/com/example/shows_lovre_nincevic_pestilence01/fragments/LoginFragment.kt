package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
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
            requireContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

    }

    override fun onResume() { //checks if the user just registered
        super.onResume()
        val justRegistered = sharedPreferences.getBoolean(Constants.REGISTERED_KEY, false)
        if (justRegistered) {
            binding.registerButton.visibility = View.GONE
            binding.login.text = "Registration \nsuccessful!"
        } else {
            binding.registerButton.visibility = View.VISIBLE
            binding.login.text = "Login"
        }

        sharedPreferences.edit().apply() {
            putBoolean(Constants.REGISTERED_KEY, false)
        }.apply()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                                "You have successfully logged in!",
                                false
                            )
                        } else {
                            parentActivity.showErrorSnackBar(
                                "Please provide valid credentials",
                                true
                            )
                            parentActivity.hideProgressDialog()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        parentActivity.showErrorSnackBar("Something went wrong!", true)
                        parentActivity.hideProgressDialog()
                    }


                })

            } else {
                binding.emailLoginText.setError("Please enter a valid email address!!")
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


    private fun toggleButtonEnabled() {

        if (binding.emailLoginText.text.toString()
                .isBlank() || binding.passwordLoginText.text.toString().length < 6
        )
            binding.loginButton.setEnabled(false)
        else binding.loginButton.setEnabled(true)
    }

}