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
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import com.example.eventify.attendeesViewModel.TicketTypeViewModel
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
    private lateinit var eventTicketRemaining: TextView
    private lateinit var eventTicketAvailable: TextView
    private lateinit var eventID: String
    private lateinit var cardviewDesc:CardView
    private lateinit var expandLayout:LinearLayout
    private lateinit var expandImage:ImageView
    private lateinit var buyTicketButton: MaterialButton
    private lateinit var eventIDViewModel: TicketTypeViewModel

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
        eventTicketRemaining = findViewById(R.id.attendeesEventTicketRemaining)
        eventTicketAvailable = findViewById(R.id.attendeesEventTicketAvailable)
        cardviewDesc = findViewById(R.id.descriptionCardView)
        expandLayout = findViewById(R.id.expandLayout)
        expandImage = findViewById(R.id.expandImage)
        buyTicketButton = findViewById(R.id.attendeesEventDetailBuyTicketButton)

        getEventAndTicketDataByIntent()
    }

    private fun initializeListener() {
        cardviewDescOnClick()
        buyTicketButtonOnClick()
    }

    private fun getEventAndTicketDataByIntent() {
        val getEventAndTicket = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL,EventModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL)
        }

        if(getEventAndTicket!=null) {
            val getEventName = getEventAndTicket.eventName
            val getEventDate = "Date: ${getEventAndTicket.eventDate}"
            val getEventLocation = "Location: ${getEventAndTicket.eventLocation}"
            val getEventDescription = getEventAndTicket.eventDescription
            val getTicketRemaining = "Ticket Remaining: ${getEventAndTicket.ticket?.ticketRemaining}"
            val getTicketAvailable = if (getEventAndTicket.ticket?.ticketAvailable == true) {
                "Ticket Available: Available"
            } else {
                "Ticket Available: Sold out"
            }

            eventID = getEventAndTicket.eventId
            eventDetailImage.setImageResource(R.color.black)
            eventDetailName.text = getEventName
            eventDetailDate.text = getEventDate
            eventDetailLocation.text = getEventLocation
            eventDetailDescription.text = getEventDescription
            eventTicketRemaining.text = getTicketRemaining
            eventTicketAvailable.text = getTicketAvailable

            eventIDViewModel = ViewModelProvider(this)[TicketTypeViewModel::class.java]
            eventIDViewModel.setEventID(getEventAndTicket.eventId)
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
            intent.putExtra("event_id",eventID)
            startActivity(intent)
        }
    }
}