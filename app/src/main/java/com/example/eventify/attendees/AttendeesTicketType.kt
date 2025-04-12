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
        val array = arrayOf("option1","option2","option3","option4","option5","option6","option7","option8","option9",
            "option1","option2","option3","option4","option5","option6","option7","option8","option9",
            "option1","option2","option3","option4","option5","option6","option7","option8","option9")

        ticketTypeRadioGroup = view.findViewById(R.id.attendeesTicketTypeRadioButton)
        radioButton = RadioButton(requireActivity())
        for (i in array.indices){
            radioButton.text = array[i]
            radioButton.id = View.generateViewId()
            radioButton.tag = array[i]

            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0,20,0,0)
            radioButton.layoutParams = params
            radioButton.textSize =18f
            ticketTypeRadioGroup.addView(radioButton)
        }


//        ticketTypeRadioGroup.setOnCheckedChangeListener{ group,checkID -.
//
//        }
//
//        val selectedId = ticketTypeRadioGroup.checkedRadioButtonId
//        if (selectedId != -1) {
//            val selectedRadioButton = view.findViewById<RadioButton>(selectedId)
//            val selectedValue = selectedRadioButton?.tag.toString()
//
//            Toast.makeText(requireActivity(), "Yang dipilih: $selectedValue", Toast.LENGTH_SHORT).show()
//        }
//
//
//

        return view
    }


}