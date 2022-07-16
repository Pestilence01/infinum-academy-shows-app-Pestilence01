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

            // The functionality to authenticate the user's email and password should also be implemented, but I do not have access to any sort of database so it is impossible this early on

            val emailPattern: Regex = Regex(".+@.+") // pattern ensures that there is at least one character before "@" and at least one character after "@", the "@" is always present

            if(emailPattern.containsMatchIn(binding.ETEmailLogin.text.toString())) {
                val intent: Intent = Intent(this, ShowsActivity::class.java)
                intent.putExtra(Constants.LOGIN_EMAIL_KEY, binding.ETEmailLogin.text.toString())
                startActivity(intent)
                //finish()      -- if the user logs in, there is no reason for the Login activity to be active but for the purpose of testing I decided to leave this as is
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