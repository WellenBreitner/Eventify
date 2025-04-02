package com.example.eventify.attendees

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import com.google.android.material.button.MaterialButton

class AttendeesEventDetail : AppCompatActivity() {

    companion object{
        const val EXTRA_EVENT_DETAIL = "EVENT_DETAIL"
    }

    private lateinit var eventDetailImage:ImageView
    private lateinit var eventDetailName:TextView
    private lateinit var eventDetailDate:TextView
    private lateinit var eventDetailLocation:TextView
    private lateinit var eventDetailDescription:TextView
    private lateinit var cardviewDesc:CardView
    private lateinit var expandLayout:LinearLayout
    private lateinit var expandImage:ImageView
    private lateinit var buyTicketButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendees_event_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
        initializeListener()

    }

    private fun initializeUI() {
        eventDetailImage = findViewById(R.id.attendeesEventDetailImage)
        eventDetailName = findViewById(R.id.attendeesEventDetailName)
        eventDetailDate = findViewById(R.id.attendeesEventDetailDate)
        eventDetailLocation = findViewById(R.id.attendeesEventDetailLocation)
        eventDetailDescription = findViewById(R.id.attendeesEventDetailDesc)
        cardviewDesc = findViewById(R.id.descriptionCardView)
        expandLayout = findViewById(R.id.expandLayout)
        expandImage = findViewById(R.id.expandImage)
        buyTicketButton = findViewById(R.id.attendeesEventDetailBuyTicketButton)

        getEventDataByIntent()
    }

    private fun initializeListener() {
        cardviewDescOnClick()
        buyTicketButtonOnClick()
    }

    private fun getEventDataByIntent() {
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
            val description = getEvent.eventDescription

            eventDetailImage.setImageResource(R.color.black)
            eventDetailName.text = name
            eventDetailDate.text = date
            eventDetailLocation.text = location
            eventDetailDescription.text = description
        }
    }

    private fun cardviewDescOnClick() {
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
    }

    private fun buyTicketButtonOnClick() {
        buyTicketButton.setOnClickListener {
            val intent = Intent(this,AttendeesPurchaseTicket::class.java)
            startActivity(intent)
        }
    }
}