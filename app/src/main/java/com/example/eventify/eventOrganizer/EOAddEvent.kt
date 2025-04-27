package com.example.eventify.eventOrganizer

import android.os.Bundle
import android.text.method.TimeKeyListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityEoAddEventBinding
import com.example.eventify.databinding.ActivityEventOrganizerDashboardBinding
import com.google.firebase.Firebase
import com.google.firebase.database.database

class EOAddEvent : AppCompatActivity() {

    private lateinit var binding: ActivityEoAddEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEoAddEventBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeListeners()
    }

    private fun initializeListeners() {
        binding.addEventTimeTimePicker.setOnClickListener {
            val dialog: DialogFragment = EventTimePicker(binding.addEventTimeTimePicker)
            dialog.show(supportFragmentManager, "Time picker dialog")
        }

        binding.addEventDateDatePicker.setOnClickListener {
            val dialog: DialogFragment = EventDatePicker(binding.addEventDateDatePicker)
            dialog.show(supportFragmentManager, "Date picker dialog")
        }


        binding.cancelAddEventButton.setOnClickListener {
            finish()
        }

        binding.addEventButton.setOnClickListener {
            val timeValue = binding.addEventTimeTimePicker.text.toString().takeIf { it != "Set time" } ?: ""
            val dateValue = binding.addEventDateDatePicker.text.toString().takeIf { it != "Set date" } ?: ""

            addEvent(
                binding.addEventNameEditText.text.toString(),
                binding.addEventDescEditText.text.toString(),
                binding.addEventLocationEditText.text.toString(),
                dateValue,
                timeValue,
                null.toString(),
                TicketModelData(null),
                null.toString()
            )
        }
    }
    private fun addEvent(
        eventName: String,
        eventDescription: String,
        eventLocation: String,
        eventDate:String,
        eventDateTime: String,
        organizerID: String,
        ticket: TicketModelData,
        eventImage: String
    ) {
        when {
            eventName.isEmpty() -> binding.addEventNameEditText.apply {
                error = "Event name can't be empty"
                requestFocus()
            }
            eventDescription.isEmpty() -> binding.addEventDescEditText.apply {
                error = "Event description can't be empty"
                requestFocus()
            }
            eventLocation.isEmpty() -> binding.addEventLocationEditText.apply {
                error = "Event location can't be empty"
                requestFocus()
            }
            eventDate.isEmpty() -> Toast.makeText(this, "date can't be empty", Toast.LENGTH_SHORT).show()
            eventDateTime.isEmpty() -> Toast.makeText(this, "time can't be empty", Toast.LENGTH_SHORT).show()
            else -> {
                val eventRef = Firebase.database.getReference("events")
                val getID = eventRef.push().key
                val newEvent = getID?.let {
                    EventModelData(
                        it,
                        eventName,
                        eventDescription,
                        "$eventDate $eventDateTime",
                        eventLocation,
                        null.toString(),
                        TicketModelData(null,it,null,null,null,null),
                        null
                    )
                }

                eventRef.child(getID.toString()).setValue(newEvent).addOnSuccessListener {
                    Toast.makeText(this, "Event Added Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener{
                    Toast.makeText(this, "Event Added Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}