package com.example.eventify.eventOrganizer

import android.app.Activity
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
import com.example.eventify.databinding.ActivityEoAddEventBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import java.io.File

class EOAddEvent : AppCompatActivity() {

    private lateinit var binding: ActivityEoAddEventBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var imageUrl: String? = null

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

        firebaseAuth = FirebaseAuth.getInstance()
        initializeListeners()
    }


    private fun uploadImage(uri: Uri) {
        MediaManager.get().upload(uri)
            .unsigned("EventifyPreset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Upload started")
                    Toast.makeText(this@EOAddEvent, "Upload started", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.d("Cloudinary", "Uploading... $bytes / $totalBytes")
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    imageUrl = resultData?.get("secure_url") as? String
                    Glide.with(applicationContext)
                        .load(imageUrl)
                        .into(binding.addClassImageView)
                    Toast.makeText(this@EOAddEvent, "Upload success", Toast.LENGTH_SHORT).show()
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e("Cloudinary", "Upload error: ${error?.description}")
                    Toast.makeText(this@EOAddEvent, "Upload failed: ${error?.description}", Toast.LENGTH_LONG).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.e("Cloudinary", "Upload rescheduled: ${error?.description}")
                }
            })
            .dispatch()

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

        binding.addEventPosterSelector.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)

        }

        binding.cancelAddEventButton.setOnClickListener {
            finish()
        }

        binding.addEventButton.setOnClickListener {
            val timeValue =
                binding.addEventTimeTimePicker.text.toString().takeIf { it != "Set time" } ?: ""
            val dateValue =
                binding.addEventDateDatePicker.text.toString().takeIf { it != "Set date" } ?: ""
            val getUser = firebaseAuth.currentUser?.uid

            addEvent(
                binding.addEventNameEditText.text.toString(),
                binding.addEventDescEditText.text.toString(),
                binding.addEventLocationEditText.text.toString(),
                dateValue,
                timeValue,
                getUser.toString(),
            )
        }
    }
    private fun addEvent(
        eventName: String,
        eventDescription: String,
        eventLocation: String,
        eventDate: String,
        eventDateTime: String,
        organizerID: String
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

            eventDate.isEmpty() -> Toast.makeText(
                this,
                "Date can't be empty",
                Toast.LENGTH_SHORT
            ).show()

            eventDateTime.isEmpty() -> Toast.makeText(
                this,
                "Time can't be empty",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val eventRef = Firebase.database.getReference("events")
                val getID = eventRef.push().key
                val newEvent = getID?.let {
                    EventModelData(
                        it,
                        eventName,
                        eventDescription,
                        eventDate,
                        eventDateTime,
                        eventLocation,
                        organizerID,
                        imageUrl,
                    )
                }

                eventRef.child(getID.toString()).setValue(newEvent)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Event Added Successfully", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Event Add Failed", Toast.LENGTH_SHORT).show()
                    }
            }
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

}