package com.example.shows_lovre_nincevic_pestilence01.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.databinding.ActivityMainBinding
import com.example.shows_lovre_nincevic_pestilence01.fragments.LoginFragment
import com.example.shows_lovre_nincevic_pestilence01.fragments.ShowsFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
    }
}