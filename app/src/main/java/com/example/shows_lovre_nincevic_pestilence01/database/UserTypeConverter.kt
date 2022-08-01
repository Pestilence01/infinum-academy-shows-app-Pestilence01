package com.example.shows_lovre_nincevic_pestilence01.database

import androidx.room.TypeConverter
import com.example.shows_lovre_nincevic_pestilence01.models.User
import org.json.JSONObject

class UserTypeConverter {  // The TypeConverter is necessary to convert User class to a JSON object
    @TypeConverter
    fun fromUser(user: User): String {
        return JSONObject().apply {
            put("id", user.id)
            put("email", user.email)
            if(user.image_url == null)
            put("image_url", "no_url")
            else
                put("image_url", user.image_url)
        }.toString()
    }

    @TypeConverter
    fun toUser(user: String): User {
        val json = JSONObject(user)
        return User(json.getString("id"), json.getString("email"), json.getString("image_url"))
    }
}
