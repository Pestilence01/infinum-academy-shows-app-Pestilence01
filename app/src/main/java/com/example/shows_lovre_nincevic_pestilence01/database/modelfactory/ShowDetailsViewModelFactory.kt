package com.example.shows_lovre_nincevic_pestilence01.database.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.shows_lovre_nincevic_pestilence01.database.ShowsDatabase
import com.example.shows_lovre_nincevic_pestilence01.viewmodels.ShowDetailsViewModel

class ShowDetailsViewModelFactory(
    val database: ShowsDatabase?
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)){
            return ShowDetailsViewModel(database!!) as T
        }
        throw IllegalArgumentException()
    }
}