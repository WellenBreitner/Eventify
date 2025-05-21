package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModelData(
    val eventId: String? = null,
    val eventName: String? = null,
    val eventDescription:  String? = null,
    val eventDate: String? = null,
    val eventTime: String? = null,
    val eventLocation: String? = null,
    val organizerId: String? = null,
    val eventImage: Int? = null,
) : Parcelable{
    constructor() : this(
        eventId = null,
        eventName = null,
        eventDescription = null,
        eventDate = null,
        eventTime = null,
        eventLocation = null,
        organizerId = null,
        eventImage = null,
    )
}