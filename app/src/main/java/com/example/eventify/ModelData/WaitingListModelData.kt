package com.example.eventify.ModelData

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class WaitingListModelData(
    val waitingListId: String? = null,
    val eventId: String ? = null,
    val userId: String ? = null,
    val email: String ? = null,
    var status: String? = null,
    var notification_sent: Boolean? = null,
    val timeJoin: String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        waitingListId = null,
        eventId = null,
        userId = null,
        email = null,
        status = null,
        notification_sent = null,
        timeJoin = null
    )
}
