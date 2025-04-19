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
//    private lateinit var ticketTypeArrayList: ArrayList<String>
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

        eventDatabaseReference.get()
            .addOnSuccessListener { dataEvent ->
                if (dataEvent.exists()) {
                    for (data in dataEvent.children) {
                        val event = data.getValue(EventModelData::class.java)
                        val ticket = event?.ticket
                        val eventTicketType = ticket?.ticketType

                        if ((eventTicketType != null) && (eventId == event.eventId)) {
                            for ((keys, value) in eventTicketType) {
                                ticketTypeHashMap[keys] = value

                                ticketTypeRadioButton = RadioButton(requireActivity())
                                ticketTypeRadioButton.text = "${keys} (${value})"
                                ticketTypeRadioButton.id = View.generateViewId()
                                ticketTypeRadioButton.tag = keys


                                if (ticketViewModel.getTicketTypeData.value == ticketTypeRadioButton.tag.toString()) {
                                        ticketTypeRadioButton.isChecked = true
                                }

                                val params = RadioGroup.LayoutParams(
                                    RadioGroup.LayoutParams.MATCH_PARENT,
                                    RadioGroup.LayoutParams.WRAP_CONTENT
                                )

                                params.setMargins(0,20,0,0)
                                ticketTypeRadioButton.layoutParams = params
                                ticketTypeRadioButton.textSize = 18f
                                ticketTypeRadioGroup.addView(ticketTypeRadioButton)
                            }

                            attendeesSaveTicketTypeButton.setOnClickListener {
                                val selectedId = ticketTypeRadioGroup.checkedRadioButtonId
                                val selectedRadioButton = view?.findViewById<RadioButton>(selectedId)
                                if (selectedRadioButton != null){
                                    val selectedValue = selectedRadioButton.tag.toString()
                                    val selectedPrice = ticketTypeHashMap.getValue(selectedValue)
                                    ticketViewModel.setTicketType(selectedValue)
                                    ticketViewModel.setTicketPrice(selectedPrice.toString())
                                    dismiss()
                                }else{
                                    Toast.makeText(requireActivity(), "You must one of the ticket type", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                }
            }
    }

}