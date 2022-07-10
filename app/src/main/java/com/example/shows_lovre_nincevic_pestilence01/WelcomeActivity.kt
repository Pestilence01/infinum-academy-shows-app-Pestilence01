package com.example.shows_lovre_nincevic_pestilence01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var email: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // this line hides the top of the screen (battery life, time, wifi...) allowing the application to take up the entire screen

        if(intent.hasExtra("EXTRA_EMAIL_KEY")){
            email = intent.getStringExtra("EXTRA_EMAIL_KEY").toString()
        }

        val result: String = "Welcome, " + email.split("@")[0] + "!"    // The split function returns a list of Strings so we can easily concatenate

        binding.TVWelcomeScreenEmail.text = result

    }
}