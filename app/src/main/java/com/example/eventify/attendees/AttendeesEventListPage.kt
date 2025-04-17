package com.example.eventify.attendees

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.attendeesAdapter.AttendeesEventAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class AttendeesEventListPage : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var events: ArrayList<EventModelData>
    private lateinit var tickets: ArrayList<TicketModelData>
    private lateinit var eventDatabaseReference: DatabaseReference
    private lateinit var ticketDatabaseReference: DatabaseReference
    private lateinit var adapter: AttendeesEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendees_event_list_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
        initializeListener()
    }

    private fun initializeUI() {
        events = ArrayList()
        tickets = ArrayList()
        recyclerView = findViewById(R.id.attendeesEventListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AttendeesEventAdapter(events)
        recyclerView.adapter = adapter

        getDataEventForAttendees()
    }

    private fun initializeListener() {
        adapter.setOnClickEventListener(object: AttendeesEventAdapter.onClickEventListener {
            override fun onClickItem(data: EventModelData) {
                onClick(data)
            }
        })

    }

    private fun getDataEventForAttendees(){
        events.clear()
        eventDatabaseReference = Firebase.database.getReference("events")

        eventDatabaseReference.get()
            .addOnSuccessListener { dataEvents ->
                if (dataEvents.exists()) {
                    for (data in dataEvents.children) {
                        val event = data.getValue(EventModelData::class.java)
                        if (event != null) {
                            events.add(EventModelData(
                                event.eventId,
                                event.eventName,
                                event.eventDescription,
                                event.eventDate,
                                event.eventLocation,
                                event.organizerId)
                            )
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("database", "No events found")
                }
            }
            .addOnFailureListener {
                Log.e("database", "Firebase error", it)
            }



        ticketDatabaseReference = Firebase.database.getReference("tickets")
        ticketDatabaseReference.get()
            .addOnSuccessListener { dataTicket ->
                if (dataTicket.exists()){
                    for (ticket in dataTicket.children){
                        val ticket = ticket.getValue(TicketModelData::class.java)
                            if (ticket != null ){

                                tickets.add(TicketModelData(
                                    ticket.ticketId,
                                    ticket.eventId,
                                    ticket.ticketType,
                                    ticket.ticketRemaining,
                                    ticket.ticketAvailable,
                                    ticket.ticketLimit))
                        }
                    }
                }else{
                    Log.e("database", "No Ticket found")
                }

            }
            .addOnFailureListener {
                Log.e("database", "Firebase error", it)
            }
        }


    fun onClick(event: EventModelData){
        val intent = Intent(this,AttendeesEventDetail::class.java)
        intent.putExtra(AttendeesEventDetail.EXTRA_EVENT_DETAIL,event)
        startActivity(intent)
    }
}