package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModelData(
    val eventId: String,
    val eventName: String,
    val eventDescription:  String,
    val eventDate: String,
    val eventLocation: String,
    val organizerId: String,
    val ticket: TicketModelData?,
    val eventImage: Int? = null,
) : Parcelable{
    constructor() : this(
        eventId = "",
        eventName = "",
        eventDescription = "",
        eventDate = "",
        eventLocation = "",
        organizerId = "",
        ticket = null,
        eventImage = null,
    )
}