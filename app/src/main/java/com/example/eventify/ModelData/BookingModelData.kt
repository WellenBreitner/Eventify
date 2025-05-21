package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingModelData(
    val bookingId: String? = null,
    val userId: String? = null,
    val userEmail:String? = null,
    val eventID:String? = null,
    val eventName: String? = null,
    val eventDate: String? = null,
    val eventLocation:String? = null,
    val ticketType:String? = null,
    val numberOfTicket: String? = null,
    val priceForEachTicket:String? =null,
    var totalPrice: Double? = null,
    val bookedAt: String? = null,
    val selectedSeat: ArrayList<String> = arrayListOf()
):Parcelable
