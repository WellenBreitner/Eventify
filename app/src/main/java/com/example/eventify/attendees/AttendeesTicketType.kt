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
import com.example.eventify.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AttendeesTicketType : BottomSheetDialogFragment() {
    private lateinit var ticketTypeRadioGroup: RadioGroup
    private lateinit var radioButton: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendees_ticket_type, container, false)

        // testing purpose
        val array = arrayOf("option1","option2","option3","option4","option5","option6","option7","option8","option9","option10",
            "option11","option12","option13","option14","option15","option16","option17","option18","option19","option20",
            "option21","option22","option23","option24","option25","option26","option27","option28","option29","option30")

        ticketTypeRadioGroup = view.findViewById(R.id.attendeesTicketTypeRadioButton)

        for (i in array.indices){
            radioButton = RadioButton(requireActivity())
            radioButton.text = array[i]
            radioButton.textSize =18f
            radioButton.id = View.generateViewId()
            radioButton.tag = array[i]

            if (array[0] == radioButton.tag.toString()){
                radioButton.isChecked = true
            }

            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0,20,0,0)
            radioButton.layoutParams = params
            radioButton.textSize =18f
            ticketTypeRadioGroup.addView(radioButton)
        }

        ticketTypeRadioGroup.setOnCheckedChangeListener{_, _ ->
            val selectedId = ticketTypeRadioGroup.checkedRadioButtonId
            val selectedRadioButton = view.findViewById<RadioButton>(selectedId)
            val selectedValue = selectedRadioButton?.tag.toString()

            Toast.makeText(requireActivity(), "Yang dipilih: $selectedValue", Toast.LENGTH_SHORT).show()

        }

        return view
    }


}