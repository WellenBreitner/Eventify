package com.example.eventify.attendees

import android.animation.LayoutTransition
import android.content.Intent
import android.graphics.Color
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
import com.example.eventify.databinding.ActivityAttendeesEventDetailBinding
import com.google.android.material.button.MaterialButton

class AttendeesEventDetail : AppCompatActivity() {

    companion object{
        const val EXTRA_EVENT_DETAIL = "EVENT_DETAIL"
    }

    private lateinit var getEventID: String
    private lateinit var getEventName: String
    private lateinit var getEventDate: String
    private lateinit var getEventLocation: String
    private lateinit var getEventDescription: String
    private lateinit var getTicketAvailable: String
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
        val getEventAndTicket = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL,EventModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL)
        }

        if(getEventAndTicket!=null) {
            getEventName = getEventAndTicket.eventName
            getEventDate = getEventAndTicket.eventDate
            getEventLocation = getEventAndTicket.eventLocation
            getEventDescription = getEventAndTicket.eventDescription
            val getTicketRemaining = "Ticket Remaining: ${getEventAndTicket.ticket?.ticketRemaining}"
            getTicketAvailable = if (getEventAndTicket.ticket?.ticketAvailable == null) {
                "Ticket Available: Not Available"
            } else if (getEventAndTicket.ticket.ticketAvailable == true) {
                "Ticket Available: Available"
            }else{
                "Ticket Available: Sold Out"
            }


            getEventID = getEventAndTicket.eventId
            binding.attendeesEventDetailImage.setImageResource(R.color.black)
            binding.attendeesEventDetailName.text = getEventName
            binding.attendeesEventDetailDate.text = "Date: $getEventDate"
            binding.attendeesEventDetailLocation.text = "Location: $getEventLocation"
            binding.attendeesEventDetailDesc.text = getEventDescription
            binding.attendeesEventTicketRemaining.text = getTicketRemaining
            binding.attendeesEventTicketAvailable.text = getTicketAvailable

            if (getTicketAvailable == "Ticket Available: Not Available"){
                binding.attendeesEventDetailBuyTicketButton.isEnabled = false
                binding.attendeesEventDetailBuyTicketButton.text = "Ticket Not Available"
                binding.attendeesEventDetailBuyTicketButton.setTextColor(Color.parseColor("#000000"))
                binding.attendeesEventDetailBuyTicketButton.setBackgroundColor(Color.parseColor("#F5F5F5"))
            }
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
            val intent = Intent(this,AttendeesPurchaseTicket::class.java)
            intent.putExtra("event_id",EventModelData(
                getEventID,
                getEventName,
                getEventDescription,
                getEventDate,
                getEventLocation,
                null,null,null))
            startActivity(intent)
        }
    }
}