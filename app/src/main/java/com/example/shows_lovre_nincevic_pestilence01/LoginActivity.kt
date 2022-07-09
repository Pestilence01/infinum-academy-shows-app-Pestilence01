package com.example.shows_lovre_nincevic_pestilence01

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // this line hides the top of the screen (battery life, time, wifi...) allowing the application to take up the entire screen

        binding.ETEmailLogin.addTextChangedListener(object : TextWatcher {


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInput(binding)
            }

            override fun afterTextChanged(s: Editable) {
                // Unnecessary
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Unnecessary
            }
        })

        binding.ETPasswordLogin.addTextChangedListener(object : TextWatcher {


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInput(binding)
            }

            override fun afterTextChanged(s: Editable) {
                // Unnecessary
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Unnecessary
            }
        })

        binding.buttonLogin.setOnClickListener {

            var emailPattern: Regex = Regex(".+@.+") // pattern ensures that there is at least one character before "@" and at least one character after "@", the "@" is always present

            if(emailPattern.containsMatchIn(binding.ETEmailLogin.text.toString())) {
                var intent: Intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra("EXTRA_EMAIL_KEY", binding.ETEmailLogin.text.toString())
                startActivity(intent)
                Toast.makeText(this, "You have successfully logged in!", Toast.LENGTH_SHORT).show()
            }
            else {
                binding.ETEmailLogin.setError("Please enter a valid email address!!")!!
            }
        }
    }

    //This function checks whether the email is blank and if the password field has less than 6 characters. If that is the case, the button will be disabled
    private fun checkInput(binding: ActivityLoginBinding) {

        if(binding.ETEmailLogin.text.toString().isBlank() || binding.ETPasswordLogin.text.toString().length < 6)
            binding.buttonLogin.setEnabled(false)!!

        else binding.buttonLogin.setEnabled(true)!!
    }


}