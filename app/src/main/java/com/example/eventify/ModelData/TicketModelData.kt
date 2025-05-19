package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TicketModelData(
    val ticketId: String? = null,
    val eventId: String? = null,
    val ticketType: String? = null,
    val ticketPrice: Int? = null,
    val ticketRemaining: Int? = null,
    val ticketAvailable: Boolean? = null,
    val ticketLimit: Int? = null,
    val promotionCode: String? = null,
    val discount: Int? = null,
    val expiryDate: String? = null,
    val maxWaitingList: Int? = null,
    val assignSeat: List<String>? = null
): Parcelable {
    constructor() : this(
        ticketId = null,
        eventId = null,
        ticketType = null,
        ticketRemaining = null,
        ticketPrice = null,
        ticketAvailable = null,
        ticketLimit = null,
        promotionCode = null,
        discount = null,
        expiryDate = null,
        maxWaitingList = 0,
        assignSeat = null
    )
}
