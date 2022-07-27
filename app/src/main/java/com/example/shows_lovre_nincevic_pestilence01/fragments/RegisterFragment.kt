package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.activities.MainActivity
import com.example.shows_lovre_nincevic_pestilence01.api.ApiModule
import com.example.shows_lovre_nincevic_pestilence01.api.requests.RegisterRequest
import com.example.shows_lovre_nincevic_pestilence01.api.responses.RegisterResponse
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var parentActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = (activity!! as MainActivity)

        ApiModule.initRetrofit(requireContext())

        binding.registerButton.setOnClickListener {

            parentActivity.showProgressDialog("Please wait")

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
                    if(response.isSuccessful){
                        parentActivity.showErrorSnackBar("You successfully registered!", false)
                    }
                    else {
                        parentActivity.showErrorSnackBar("Email already taken! Use a different one", true)
                    }
                    parentActivity.hideProgressDialog()
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    parentActivity.showErrorSnackBar("Something went wrong!", true)
                    parentActivity.hideProgressDialog()
                }

            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

}