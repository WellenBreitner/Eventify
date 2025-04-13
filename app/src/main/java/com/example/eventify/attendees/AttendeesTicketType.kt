package com.example.eventify.attendees

import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.constraintlayout.widget.Group
import androidx.core.view.marginTop
import androidx.lifecycle.ViewModelProvider
import com.example.eventify.R
import com.example.eventify.attendeesViewModel.TicketTypeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton


class AttendeesTicketType : BottomSheetDialogFragment() {
    private lateinit var ticketTypeRadioGroup: RadioGroup
    private lateinit var attendeesSaveTicketTypeButton : MaterialButton
    private lateinit var ticketTypeRadioButton: RadioButton
    private lateinit var taskViewModel: TicketTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendees_ticket_type, container, false)

        // testing purpose
        val dummyData = hashMapOf(
            "lecture" to 500,
            "child" to 1000,
            "adult" to 1500,
            "VIP" to 5000)

        val arrayDummy = dummyData.keys.toList()
        ticketTypeRadioGroup = view.findViewById(R.id.attendeesTicketTypeRadioButton)
        attendeesSaveTicketTypeButton = view.findViewById(R.id.attendeesSaveTicketTypeButton)
        taskViewModel = ViewModelProvider(requireActivity())[TicketTypeViewModel::class.java]

        for ((key,value) in dummyData) {
            ticketTypeRadioButton = RadioButton(requireActivity())
            ticketTypeRadioButton.text = "${key} (${value})"
            ticketTypeRadioButton.textSize = 18f
            ticketTypeRadioButton.id = View.generateViewId()
            ticketTypeRadioButton.tag = key

            if (taskViewModel.selectedData.value == null) {
                if (arrayDummy[0] == ticketTypeRadioButton.tag.toString()) {
                    ticketTypeRadioButton.isChecked = true
                }
            } else{
                if (taskViewModel.selectedData.value == ticketTypeRadioButton.tag.toString()) {
                    ticketTypeRadioButton.isChecked = true
                }
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
            val selectedRadioButton = view.findViewById<RadioButton>(selectedId)
            val selectedValue = selectedRadioButton?.tag.toString()


            taskViewModel.selectedData.value = selectedValue
            dismiss()
        }

        return view
    }


}