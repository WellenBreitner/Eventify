package com.example.eventify.attendees

import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Mms.Intents
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R

class AttendeesEventDetail : AppCompatActivity() {

    companion object{
        const val EXTRA_EVENT_DETAIL = "EVENT_DETAIL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendees_event_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val eventDetailImage:ImageView = findViewById(R.id.attendeesEventDetailImage)
        val eventDetailName:TextView = findViewById(R.id.attendeesEventDetailName)
        val eventDetailDate:TextView = findViewById(R.id.attendeesEventDetailDate)
        val eventDetailDescription:TextView = findViewById(R.id.attendeesEventDetailDesc)

        val getEvent = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL,EventModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL)
        }


        if(getEvent!=null){
            eventDetailImage.setImageResource(R.color.black)
            eventDetailName.text = getEvent.eventName
            eventDetailDate.text = getEvent.eventDate
            eventDetailDescription.text = getEvent.eventDescription
        }
    }
}