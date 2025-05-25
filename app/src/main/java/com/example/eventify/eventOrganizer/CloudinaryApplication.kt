package com.example.eventify.eventOrganizer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.cloudinary.android.MediaManager

class CloudinaryApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val config = HashMap<String, String>()
        config["cloud_name"] = "dhgwg6qmf"
        MediaManager.init(this, config)
    }
}