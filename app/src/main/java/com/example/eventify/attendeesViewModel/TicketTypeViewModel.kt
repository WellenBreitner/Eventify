package com.example.eventify.attendeesViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicketTypeViewModel : ViewModel(){
    private val _ticketTypeData = MutableLiveData<String>()
    val getTicketTypeData : LiveData<String> get() = _ticketTypeData
    fun setTicketType(ticketType:String){
        _ticketTypeData.value = ticketType
    }

    private val _ticketPriceData = MutableLiveData<String>()
    val getTicketPriceData : LiveData<String> get() = _ticketPriceData
    fun setTicketPrice(ticketPrice:String){
        _ticketPriceData.value = ticketPrice
    }

    private val _EventID = MutableLiveData<String>()
    val getEventId : LiveData<String> get() = _EventID
    fun setEventID(eventId:String){
        _EventID.value = eventId
    }
}