package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TicketModelData(
    val ticketId: String? = null,
    val eventId: String? = null,
    val ticketType: HashMap<String, Int>? = null,
    val ticketRemaining: Int? = null,
    val ticketAvailable: Boolean? = null,
    val ticketLimit: Int? = null,
    val promotionCode: String? = null,
    val discount: Int? = null
): Parcelable {
    constructor() : this(
        ticketId = null,
        eventId = null,
        ticketType = null,
        ticketRemaining = null,
        ticketAvailable = null,
        ticketLimit = null,
        promotionCode = null,
        discount = null
    )
}
