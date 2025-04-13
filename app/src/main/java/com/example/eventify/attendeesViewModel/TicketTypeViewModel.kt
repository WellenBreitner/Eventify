package com.example.eventify.attendeesViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicketTypeViewModel : ViewModel(){
    val selectedData = MutableLiveData<String>()
}