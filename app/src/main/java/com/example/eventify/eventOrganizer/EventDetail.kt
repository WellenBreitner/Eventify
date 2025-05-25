package com.example.eventify.eventOrganizer

import android.animation.LayoutTransition
import android.content.Intent
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
import com.example.eventify.databinding.ActivityEventDetailBinding


class EventDetail : AppCompatActivity() {
    companion object {
        const val EXTRA_EVENT_DETAIL  = "EVENT_DETAIL"
        const val EXTRA_TICKET_DETAIL = "TICKET_DETAIL"
        const val EXTRA_TICKET_TOTAL  = "TICKET_TOTAL"
    }

    private lateinit var binding: ActivityEventDetailBinding

    private lateinit var eventData: EventModelData
    private lateinit var ticketData: TicketModelData
    private var totalTickets: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        extractIntentData()

        bindDataToViews()

        setupDescriptionToggle()
        setupBottomButton()
    }

    private fun extractIntentData() {
        eventData = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL, EventModelData::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EVENT_DETAIL)!!
        }

        ticketData = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_TICKET_DETAIL, TicketModelData::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TICKET_DETAIL)!!
        }

        totalTickets = if (Build.VERSION.SDK_INT >= 33) {
            intent.getIntExtra(EXTRA_TICKET_TOTAL, 0)
        } else {
            @Suppress("DEPRECATION")
            intent.getIntExtra(EXTRA_TICKET_TOTAL, 0)
        }
    }

    private fun bindDataToViews() {
        Glide.with(this)
            .load(eventData.eventImage)
            .placeholder(R.drawable.event_default_image)
            .into(binding.eventDetailImage)

        binding.eventDetailName.text     = eventData.eventName
        binding.eventDetailDate.text     = "Date: ${eventData.eventDate} ${eventData.eventTime}"
        binding.eventDetailLocation.text = "Location: ${eventData.eventLocation}"
        binding.eventDetailDesc.text     = eventData.eventDescription

        val remainingText = "Ticket Remaining: $totalTickets"
        binding.eventTicketRemaining.text = remainingText

        val isAvailable = totalTickets > 0
        binding.eventTicketAvailable.text =
            if (isAvailable) "Ticket Available: Available"
            else "Ticket Available: Sold Out"
    }

    private fun setupDescriptionToggle() {
        binding.eventExpandLayout.layoutTransition
            .enableTransitionType(LayoutTransition.CHANGING)

        binding.eventExpandImage.setImageResource(R.drawable.down_icon)

        binding.eventDescriptionCardView.setOnClickListener {
            val desc = binding.eventDetailDesc
            val isHidden = desc.visibility == View.GONE

            binding.eventExpandImage.setImageResource(
                if (isHidden) R.drawable.up_icon
                else R.drawable.down_icon
            )
            TransitionManager.beginDelayedTransition(
                binding.eventExpandLayout, AutoTransition()
            )
            desc.visibility = if (isHidden) View.VISIBLE else View.GONE
        }
    }

    private fun setupBottomButton() {
        binding.editEventButton.setOnClickListener {
            val intent = Intent(this, EventEditPage::class.java).apply {
                putExtra(
                    EventDetail.EXTRA_EVENT_DETAIL,
                    EventModelData(
                        eventData.eventId,
                        eventData.eventName,
                        eventData.eventDescription,
                        eventData.eventDate,
                        eventData.eventTime,
                        eventData.eventLocation,
                        eventData.organizerId,
                        eventData.eventImage
                    )
                )
            }
            startActivity(intent)
        }
    }
}
