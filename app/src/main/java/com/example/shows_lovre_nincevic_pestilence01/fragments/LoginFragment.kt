package com.example.shows_lovre_nincevic_pestilence01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.databinding.FragmentLoginBinding
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants



class LoginFragment : Fragment(R.layout.fragment_login) {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding.emailLoginText.doOnTextChanged { _, _, _, _ ->
            toggleButtonEnabled()
        }

        binding.passwordLoginText.doOnTextChanged { _, _, _, _ ->
            toggleButtonEnabled()
        }
        binding.loginButton.setOnClickListener {


            val emailPattern: Regex =
                Regex(".+@.+[.].*") // pattern ensures that there is at least one character before "@" and at least one character after "@", the "@" is always present. Updated to account for the domain

            if (emailPattern.containsMatchIn(binding.emailLoginText.text.toString())) {

                val username: String = binding.emailLoginText.text!!.split("@")[0]   // Username is the string before "@"
                findNavController().navigate(R.id.action_loginFragment_to_showsFragment, bundleOf(Constants.LOGIN_EMAIL_KEY to username))
                Toast.makeText(activity, "You have successfully logged in!", Toast.LENGTH_SHORT).show()
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
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
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