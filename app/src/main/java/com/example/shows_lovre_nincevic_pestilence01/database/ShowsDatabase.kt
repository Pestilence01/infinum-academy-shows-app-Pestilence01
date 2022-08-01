package com.example.shows_lovre_nincevic_pestilence01.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shows_lovre_nincevic_pestilence01.database.daos.ReviewDao
import com.example.shows_lovre_nincevic_pestilence01.database.daos.ShowDao
import com.example.shows_lovre_nincevic_pestilence01.database.entities.ReviewEntity
import com.example.shows_lovre_nincevic_pestilence01.database.entities.ShowEntity

@Database(
    entities = [
        ShowEntity::class,
        ReviewEntity::class
    ],
    version = 4
        )
@TypeConverters(UserTypeConverter::class)  //used type converter to save custom User class to DB
abstract class ShowsDatabase: RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: ShowsDatabase? = null

        fun getDatabase(context: Context): ShowsDatabase? {
            return INSTANCE ?: synchronized(this){
                val database = Room.databaseBuilder(
                    context,
                    ShowsDatabase::class.java,
                    "shows_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = database
                database
            }
        }

    }

    abstract fun showDao(): ShowDao

    abstract fun reviewDao(): ReviewDao
}