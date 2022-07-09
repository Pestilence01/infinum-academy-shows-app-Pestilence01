package com.example.shows_lovre_nincevic_pestilence01

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityLoginBinding
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var email: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("EXTRA_EMAIL_KEY")){
            email = intent.getStringExtra("EXTRA_EMAIL_KEY").toString()
        }

        binding.TVWelcomeScreenEmail.text = email
    }
}