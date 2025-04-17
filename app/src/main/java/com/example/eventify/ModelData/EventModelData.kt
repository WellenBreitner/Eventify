package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class EventModelData(
    val eventId: String,
    val eventName: String,
    val eventDescription:  String,
    val eventDate: String,
    val eventLocation: String,
    val organizerId: String,
    val eventImage: Int? = null
) : Parcelable{
    constructor() : this(
        eventId = "",
        eventName = "",
        eventDescription = "",
        eventDate = "",
        eventLocation = "",
        organizerId = "",
        eventImage = null,
    )
}