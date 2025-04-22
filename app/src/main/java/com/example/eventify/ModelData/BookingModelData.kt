package com.example.eventify.ModelData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingModelData(
    val bookingId: String? = null,
    val userId: String? = null,
    val username:String? = null,
    val userEmail:String? = null,
    val eventID:String? = null,
    val ticketType:String? = null,
    val numberOfTicket: String? = null,
    val priceForEachTicket:String? =null,
    val totalPrice:Int? = null,
    val selectedSeat: ArrayList<String> = arrayListOf()
):Parcelable
