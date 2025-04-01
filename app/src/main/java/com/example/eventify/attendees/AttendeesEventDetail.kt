package com.example.eventify.attendees

import android.animation.LayoutTransition
import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Mms.Intents
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import org.w3c.dom.Text

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
        val eventDetailLocation:TextView = findViewById(R.id.attendeesEventDetailLocation)
        val ticketAvailable:TextView = findViewById(R.id.attendeesEventDetailTicketAvailable)
        val eventDetailDescription:TextView = findViewById(R.id.attendeesEventDetailDesc)
        val cardviewDesc:CardView = findViewById(R.id.descriptionCardView)
        val expandLayout:LinearLayout = findViewById(R.id.expandLayout)
        val expandImage:ImageView = findViewById(R.id.expandImage)

        expandLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        expandImage.setBackgroundResource(R.drawable.down_icon)

        cardviewDesc.setOnClickListener {
            if(eventDetailDescription.visibility == View.GONE){
                expandImage.setBackgroundResource(R.drawable.up_icon)
                TransitionManager.beginDelayedTransition(expandLayout, AutoTransition())
                eventDetailDescription.visibility = View.VISIBLE
            }else{
                expandImage.setBackgroundResource(R.drawable.down_icon)
                eventDetailDescription.visibility = View.GONE
                TransitionManager.beginDelayedTransition(expandLayout, AutoTransition())
            }
        }

        val getEvent = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL,EventModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL)
        }


        if(getEvent!=null){
            val name = getEvent.eventName
            val date = "Date: ${ getEvent.eventDate}"
            val location = "Location: ${getEvent.eventLocation}"
            val available = "Ticket: " + if (getEvent.ticketAvailable) "Available" else "Sold out"
            val description = getEvent.eventDescription

            eventDetailImage.setImageResource(R.color.black)
            eventDetailName.text = name
            eventDetailDate.text = date
            eventDetailLocation.text = location
            ticketAvailable.text = available
            eventDetailDescription.text = description

        }
    }
}