package com.example.shows_lovre_nincevic_pestilence01.application

import android.app.Application
import com.example.shows_lovre_nincevic_pestilence01.database.ShowsDatabase
import java.util.concurrent.Executors

class ShowsApplication: Application() {

    val database by lazy {
        ShowsDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()

        Executors.newSingleThreadExecutor().execute{

        }
    }

}