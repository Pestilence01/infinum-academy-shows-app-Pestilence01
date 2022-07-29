package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.requests.RegisterRequest
import com.example.shows_lovre_nincevic_pestilence01.api.responses.RegisterResponse
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentRegisterBinding
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var parentActivity: MainActivity
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

        parentActivity = (activity!! as MainActivity)

        ApiModule.initRetrofit(requireContext())

        binding.emailRegisterText.doOnTextChanged { _, _, _, _ ->
            toggleButtonEnabled()
        }

        binding.passwordRegisterText.doOnTextChanged { _, _, _, _ ->
            toggleButtonEnabled()
        }

        binding.repeatPasswordRegisterText.doOnTextChanged { _, _, _, _ ->
            toggleButtonEnabled()
        }

        binding.registerButton.setOnClickListener {

            val emailPattern: Regex = Regex(".+@.+[.].*")

            if (binding.passwordRegisterText.text.toString() != binding.repeatPasswordRegisterText.text.toString()) {
                parentActivity.showErrorSnackBar("Passwords do not match!", true)
            } else if (emailPattern.containsMatchIn(binding.emailRegisterText.text.toString())) {
                parentActivity.showProgressDialog()

                val registerRequest = RegisterRequest(
                    email = binding.emailRegisterText.text.toString(),
                    password = binding.passwordRegisterText.text.toString(),
                    passwordConfirmation = binding.repeatPasswordRegisterText.text.toString()
                )
                ApiModule.retrofit.registerUser(registerRequest).enqueue(object :
                    Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            parentActivity.showErrorSnackBar("You successfully registered!", false)
                            sharedPreferences.edit().apply() {
                                putBoolean(Constants.REGISTERED_KEY, true)
                            }.apply()
                            findNavController().popBackStack()
                        } else {
                            parentActivity.showErrorSnackBar(
                                "Email already taken! Use a different one",
                                true
                            )
                        }
                        parentActivity.hideProgressDialog()
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        parentActivity.showErrorSnackBar("Something went wrong!", true)
                        parentActivity.hideProgressDialog()
                    }

                })
            } else {
                binding.emailRegisterText.setError("Please provide a valid email address!")
            }
        }
    }

    private fun toggleButtonEnabled() {
        if (binding.emailRegisterText.text.toString()
                .isBlank() || binding.passwordRegisterText.text.toString().length < 6 || binding.repeatPasswordRegisterText.text.toString().length < 6
        )
            binding.registerButton.setEnabled(false)
        else binding.registerButton.setEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

}