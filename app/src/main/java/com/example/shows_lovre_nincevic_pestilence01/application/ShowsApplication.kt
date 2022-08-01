package com.example.shows_lovre_nincevic_pestilence01.application

import android.app.Application
import com.example.shows_lovre_nincevic_pestilence01.database.ShowsDatabase

class ShowsApplication: Application() {

    val database by lazy {
        ShowsDatabase.getDatabase(this)
    }


}