package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModelData(
    val eventImage: Int? = null,
    val eventId: String,
    val eventName: String,
    val eventDescription:  String,
    val eventDate: String,
    val organizerId: String
) : Parcelable