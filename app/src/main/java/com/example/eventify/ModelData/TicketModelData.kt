package com.example.eventify.ModelData

data class TicketModelData(
    val ticketId: String,
    val eventId: String,
    val ticketType: String,
    val ticketPrice: Int,
    val totalTicket: Int,
    val ticketRemaining: Int,
    val ticketAvailable: Boolean,
    // val ticket limit : Int,
)
