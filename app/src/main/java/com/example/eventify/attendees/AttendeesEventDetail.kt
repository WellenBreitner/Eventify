package com.example.eventify.attendees

import android.animation.LayoutTransition
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAttendeesEventDetailBinding

class AttendeesEventDetail : AppCompatActivity() {

    companion object{
        const val EXTRA_EVENT_DETAIL = "EVENT_DETAIL"
        const val EXTRA_TICKET_DETAIL = "TICKET_DETAIL"
        const val EXTRA_TICKET_TOTAL = "TICKET_TOTAL"
    }

    private lateinit var getEventID: String
    private lateinit var getEventName: String
    private lateinit var getEventDate: String
    private lateinit var getEventTime: String
    private lateinit var getOrganizerID : String
    private lateinit var getImage : String
    private lateinit var getEventLocation: String
    private lateinit var getEventDescription: String
    private lateinit var getTicketAvailable: String
    private var getTicketRemaining: Int = 0
    private var getMaxWaitingList: Int = 0
    private lateinit var binding : ActivityAttendeesEventDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAttendeesEventDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
        initializeListener()

    }

    private fun initializeUI() {
        getEventAndTicketDataByIntent()
    }

    private fun initializeListener() {
        cardviewDescOnClick()
        buyTicketButtonOnClick()
    }

    private fun getEventAndTicketDataByIntent() {
        val getEvent = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL,EventModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL)
        }

        val getTicket = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra(EXTRA_TICKET_DETAIL,TicketModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TICKET_DETAIL)
        }

        val getTotalTicket = if (Build.VERSION.SDK_INT >= 33){
            intent.getIntExtra(EXTRA_TICKET_TOTAL,0)
        }else{
            @Suppress("DEPRECATION")
            intent.getIntExtra(EXTRA_TICKET_TOTAL,0)
        }

        if(getEvent != null && getTicket != null) {
            getEventName = getEvent.eventName.toString()
            getEventDate = getEvent.eventDate.toString()
            getEventTime = getEvent.eventTime.toString()
            getOrganizerID = getEvent.organizerId.toString()
            getEventLocation = getEvent.eventLocation.toString()
            getEventDescription = getEvent.eventDescription.toString()
            getImage = getEvent.eventImage.toString()
            val getTicketRemainingText = "Ticket Remaining: $getTotalTicket"
            getTicketRemaining = getTotalTicket
            getMaxWaitingList = getTicket.maxWaitingList ?:0
            getTicketAvailable = if (getTicketRemaining > 0) {
                "Ticket Available: Available"
            }else{
                "Ticket Available: Sold Out"
            }


            getEventID = getEvent.eventId.toString()
            binding.attendeesEventDetailImage.setImageResource(R.color.black)
            binding.attendeesEventDetailName.text = getEventName
            binding.attendeesEventDetailDate.text = "Date: $getEventDate $getEventTime"
            binding.attendeesEventDetailLocation.text = "Location: $getEventLocation"
            binding.attendeesEventDetailDesc.text = getEventDescription
            binding.attendeesEventTicketRemaining.text = getTicketRemainingText
            binding.attendeesEventTicketAvailable.text = getTicketAvailable

            if(getTicketAvailable == "Ticket Available: Sold Out"){
                binding.attendeesEventDetailBuyTicketButton.text = "Join Waiting List"
                binding.attendeesEventDetailBuyTicketButton.setBackgroundColor(Color.parseColor("#A62C2C"))
            }

            Glide.with(this)
                .load(getImage)
                .placeholder(R.drawable.event_default_image)
                .into(binding.attendeesEventDetailImage)
        }

    }

    private fun cardviewDescOnClick() {
        binding.expandLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.expandImage.setBackgroundResource(R.drawable.down_icon)
        binding.descriptionCardView.setOnClickListener {
            if(binding.attendeesEventDetailDesc.visibility == View.GONE){
                binding.expandImage.setBackgroundResource(R.drawable.up_icon)
                TransitionManager.beginDelayedTransition(binding.expandLayout, AutoTransition())
                binding.attendeesEventDetailDesc.visibility = View.VISIBLE
            }else{
                binding.expandImage.setBackgroundResource(R.drawable.down_icon)
                binding.attendeesEventDetailDesc.visibility = View.GONE
                TransitionManager.beginDelayedTransition(binding.expandLayout, AutoTransition())
            }
        }
    }

    private fun buyTicketButtonOnClick() {
        binding.attendeesEventDetailBuyTicketButton.setOnClickListener {
            if (getTicketRemaining > 0) {
                val intent = Intent(this, AttendeesPurchaseTicket::class.java)
                intent.putExtra(
                    "event_id", EventModelData(
                        getEventID,
                        getEventName,
                        getEventDescription,
                        getEventDate,
                        getEventTime,
                        getEventLocation,
                        getOrganizerID,
                        getImage
                    )
                )
                startActivity(intent)
            }else{
                val addEmailDialog = AttendeesEventWaitingListEmail(getEventID, getMaxWaitingList)
                addEmailDialog.show(supportFragmentManager,"add_email_dialog")
            }
        }
    }
}