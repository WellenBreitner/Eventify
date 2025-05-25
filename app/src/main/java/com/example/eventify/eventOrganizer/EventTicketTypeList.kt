package com.example.eventify.eventOrganizer

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityEventTicketTypeListBinding
import com.example.eventify.eoAdapter.EOTicketList
import com.google.firebase.database.FirebaseDatabase

class EventTicketTypeList : AppCompatActivity() {
    private lateinit var binding : ActivityEventTicketTypeListBinding
    private lateinit var ticketList:  ArrayList<TicketModelData>
    private lateinit var adapter: EOTicketList
    private var getEventId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEventTicketTypeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
        initializeListener()
    }

    private fun initializeListener() {
        binding.eoAddTicketTypeButton.setOnClickListener {
            val intent = Intent(this,TicketSetupPage::class.java)
            intent.putExtra("eventIDForTicketSetup",getEventId)
            startActivity(intent)
        }
    }

    private fun initializeUI() {
        ticketList = arrayListOf()
        binding.ticketTypeRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EOTicketList(ticketList)
        binding.ticketTypeRecyclerView.adapter = adapter

        getEventId = intent.getStringExtra("EXTRA_EVENT_ID")

        getTicketData(getEventId.toString())

    }

    private fun getTicketData(eventID:String) {
        ticketList.clear()
        val eventRef = FirebaseDatabase.getInstance().getReference("ticket")
        eventRef.get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    for (data in snapshot.children){
                        val ticket = data.getValue(TicketModelData::class.java)
                        if (ticket != null && ticket.eventId == eventID){

                            ticketList.add(0,
                                TicketModelData(
                                ticket.ticketId,
                                ticket.eventId,
                                ticket.ticketType,
                                ticket.ticketPrice,
                                ticket.ticketRemaining,
                                ticket.ticketAvailable,
                                ticket.ticketLimit,
                                ticket.promotionCode,
                                ticket.discount,
                                ticket.expiryDate,
                                    ticket.maxWaitingList,
                                    ticket.assignSeat
                            ))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}