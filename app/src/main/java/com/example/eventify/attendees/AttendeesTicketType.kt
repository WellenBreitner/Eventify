package com.example.eventify.attendees

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.attendeesViewModel.TicketTypeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.common.base.Ticker
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlin.math.log


class AttendeesTicketType : BottomSheetDialogFragment() {
    private lateinit var ticketTypeRadioGroup: RadioGroup
    private lateinit var attendeesSaveTicketTypeButton : MaterialButton
    private lateinit var ticketTypeRadioButton: RadioButton
    private lateinit var ticketViewModel: TicketTypeViewModel
    private lateinit var ticketTypeHashMap: HashMap<String,Int>
    private lateinit var eventId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendees_ticket_type, container, false)

        ticketTypeHashMap = hashMapOf()
        ticketTypeRadioGroup = view.findViewById(R.id.attendeesTicketTypeRadioButton)
        attendeesSaveTicketTypeButton = view.findViewById(R.id.attendeesSaveTicketTypeButton)
        ticketViewModel = ViewModelProvider(requireActivity())[TicketTypeViewModel::class.java]
        ticketViewModel.getEventId.observe(requireActivity()) { data ->
            eventId = data
            getTicketType()
        }

        return view
    }

    private fun getTicketType() {
        ticketTypeHashMap.clear()

        val eventDatabaseReference: DatabaseReference = Firebase.database.getReference("events")
        val ticketDatabaseReference: DatabaseReference = Firebase.database.getReference("ticket")

        eventDatabaseReference.get()
            .addOnSuccessListener { dataEvent ->
                if (dataEvent.exists()) {
                    ticketDatabaseReference.get().addOnSuccessListener { ticketSnapShot ->
                        if (ticketSnapShot.exists()) {
                            val context = activity ?: return@addOnSuccessListener
                            for (data in dataEvent.children) {
                                val event = data.getValue(EventModelData::class.java)
                                for (dataTicket in ticketSnapShot.children) {
                                    val ticket = dataTicket.getValue(TicketModelData::class.java)
                                    val eventTicketType = ticket?.ticketType
                                    val eventTicketPrice = ticket?.ticketPrice

                                    if ((eventTicketType != null) &&
                                        (eventTicketPrice != null) &&
                                        (eventId == event?.eventId) &&
                                        (ticket.eventId == event.eventId)) {

                                        ticketTypeHashMap[eventTicketType] = eventTicketPrice.toInt()

                                        ticketTypeRadioButton = RadioButton(context).apply {
                                            text = "$eventTicketType ($eventTicketPrice)"
                                            textSize = 18f
                                            id = View.generateViewId()
                                            tag = eventTicketType
                                            setPadding(10, 10, 10, 10)
                                        }

                                        if (ticketViewModel.getTicketTypeData.value == ticketTypeRadioButton.tag.toString()) {
                                            ticketTypeRadioButton.isChecked = true
                                        }

                                        ticketTypeRadioGroup.addView(ticketTypeRadioButton)
                                    }

                                    attendeesSaveTicketTypeButton.setOnClickListener {
                                        val selectedId = ticketTypeRadioGroup.checkedRadioButtonId
                                        val selectedRadioButton = view?.findViewById<RadioButton>(selectedId)
                                        if (selectedRadioButton != null) {
                                            val selectedValue = selectedRadioButton.tag.toString()
                                            val selectedPrice = ticketTypeHashMap.getValue(selectedValue)
                                            ticketViewModel.setTicketType(selectedValue)
                                            ticketViewModel.setTicketPrice(selectedPrice.toString())
                                            dismiss()
                                        } else {
                                            Toast.makeText(context, "You must choose one of the ticket types", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

}