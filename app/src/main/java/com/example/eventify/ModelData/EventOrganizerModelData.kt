package com.example.eventify.ModelData

data class EventOrganizerModelData(
    val eventOrganizerID: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val organization: String? = "",
    val role: String = "event organizer",
    var firstTimeLogin: Boolean = true,

)
