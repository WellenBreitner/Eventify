package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class TicketModelData(
    val ticketId: String,
    val eventId: String,
    val ticketType: HashMap<String, Int>,
    val ticketRemaining: Int,
    val ticketAvailable: Boolean,
    val ticketLimit: Int,
):Parcelable{
    constructor() : this(
        ticketId = "",
        eventId = "",
        ticketType = hashMapOf(),
        ticketRemaining = 0,
        ticketAvailable = false,
        ticketLimit = 0
    )
}
