package com.example.eventify.eventOrganizer

import android.app.Application
import com.cloudinary.android.MediaManager

class CloudinaryApplication: Application(){

    override fun onCreate() {
        super.onCreate()

        val config = HashMap<String, String>()
        config["cloud_name"] = "dhgwg6qmf"
        MediaManager.init(this, config)
    }
}