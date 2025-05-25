package com.example.eventify.eventOrganizer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityEventEditPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class  EventEditPage : AppCompatActivity() {

    private lateinit var binding: ActivityEventEditPageBinding
    private  var eventData: EventModelData? = null
    private var imageUrl: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventEditPageBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        initializeListeners()

        eventData = if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EventDetail.EXTRA_EVENT_DETAIL, EventModelData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EventDetail.EXTRA_EVENT_DETAIL)
        }

        eventData?.let {
            binding.editEventNameEditText.setText(it.eventName)
            binding.editEventDescEditText.setText(it.eventDescription)
            binding.editEventLocationEditText.setText(it.eventLocation)
            binding.editEventDateDatePicker.text = it.eventDate
            binding.editEventTimeTimePicker.text = it.eventTime
            Glide.with(this)
                .load(it.eventImage)
                .placeholder(R.drawable.event_default_image)
                .into(binding.editEventImageView)
        }
    }
    private fun initializeListeners() {
        binding.editEventPosterSelector.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        binding.editEventTimeTimePicker.setOnClickListener {
            val dialog: DialogFragment = EventTimePicker(binding.editEventTimeTimePicker)
            dialog.show(supportFragmentManager, "Time picker dialog")
        }

        binding.editEventDateDatePicker.setOnClickListener {
            val dialog: DialogFragment = EventDatePicker(binding.editEventDateDatePicker)
            dialog.show(supportFragmentManager, "Date picker dialog")
        }


        binding.cancelEditEventButton.setOnClickListener {
            finish()
        }

        binding.saveEditedEventButton.setOnClickListener {
            val timeValue = binding.editEventTimeTimePicker.text.toString().takeIf { it != "Set time" } ?: ""
            val dateValue = binding.editEventDateDatePicker.text.toString().takeIf { it != "Set date" } ?: ""
            val getUser = firebaseAuth.currentUser?.uid

            editEvent(
                binding.editEventNameEditText.text.toString(),
                binding.editEventDescEditText.text.toString(),
                binding.editEventLocationEditText.text.toString(),
                dateValue,
                timeValue
            )
        }
    }

    private fun editEvent(
        eventName: String,
        eventDescription: String,
        eventLocation: String,
        eventDate: String,
        eventDateTime: String,
    ) {
        if (
            eventName.isEmpty() &&
            eventDescription.isEmpty() &&
            eventLocation.isEmpty() &&
            eventDate.isEmpty() &&
            eventDateTime.isEmpty()
        ) {
            binding.editEventNameEditText.error = "Must edit at least one field"
            binding.editEventDescEditText.error = "Must edit at least one field"
            binding.editEventLocationEditText.error = "Must edit at least one field"
            binding.editEventNameEditText.requestFocus()
            return
        }

        val eventId = eventData?.eventId ?: return
        val eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId)

        var isUpdated = false

        if (eventName.isNotEmpty()) {
            eventRef.child("eventName").setValue(eventName)
            isUpdated = true
        }
        if (eventDescription.isNotEmpty()) {
            eventRef.child("eventDescription").setValue(eventDescription)
            isUpdated = true
        }
        if (eventLocation.isNotEmpty()) {
            eventRef.child("eventLocation").setValue(eventLocation)
            isUpdated = true
        }
        if (eventDate.isNotEmpty()) {
            eventRef.child("eventDate").setValue(eventDate)
            isUpdated = true
        }
        if (eventDateTime.isNotEmpty()) {
            eventRef.child("eventTime").setValue(eventDateTime)
            isUpdated = true
        }

        if (imageUrl != null){
            eventRef.child("eventImage").setValue(imageUrl)
            isUpdated = true
        }

        if (isUpdated) {
            Toast.makeText(this, "Event edited successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Event was not edited", Toast.LENGTH_SHORT).show()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            if (it.data != null){
                val selectedImageURI = it.data?.data
                selectedImageURI?.let { uri ->
                    uploadImage(uri)
                }
            }
        }
    }

    private fun uploadImage(uri: Uri) {
        MediaManager.get().upload(uri)
            .unsigned("EventifyPreset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Upload started")
                    Toast.makeText(this@EventEditPage, "Upload started", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.d("Cloudinary", "Uploading... $bytes / $totalBytes")
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    imageUrl = resultData?.get("secure_url") as? String
                    Glide.with(applicationContext)
                        .load(imageUrl)
                        .into(binding.editEventImageView)
                    Toast.makeText(this@EventEditPage, "Upload success", Toast.LENGTH_SHORT).show()
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e("Cloudinary", "Upload error: ${error?.description}")
                    Toast.makeText(this@EventEditPage, "Upload failed: ${error?.description}", Toast.LENGTH_LONG).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.e("Cloudinary", "Upload rescheduled: ${error?.description}")
                }
            })
            .dispatch()

    }

}