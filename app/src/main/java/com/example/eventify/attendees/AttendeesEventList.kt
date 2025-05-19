package com.example.eventify.attendees

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.attendeesAdapter.AttendeesEventAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.ArrayList


class AttendeesEventList : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var events: ArrayList<EventModelData>
    private lateinit var ticket: ArrayList<TicketModelData>
    private lateinit var adapter: AttendeesEventAdapter
    private lateinit var view : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_attendees_event_list, container, false)

        initializeUI()
        initializeListener()
        return view
    }

    private fun initializeUI() {
        events = ArrayList()
        ticket = ArrayList()
        recyclerView = view.findViewById(R.id.attendeesEventListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = AttendeesEventAdapter(events,ticket)
        recyclerView.adapter = adapter

        getDataEventForAttendees()
    }

    private fun initializeListener() {
        adapter.setOnClickEventListener(object: AttendeesEventAdapter.onClickEventListener {
            override fun onClickItem(dataEvent: EventModelData, dataTicket: TicketModelData) {
                onClick(dataEvent, dataTicket)
            }
        })
    }

    private fun getDataEventForAttendees() {
        events.clear()
        ticket.clear()

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = calendar.time

        val eventDatabaseReference = Firebase.database.getReference("events")
        val ticketDatabaseReference = Firebase.database.getReference("ticket")

        eventDatabaseReference.get().addOnSuccessListener { dataEvents ->
            if (dataEvents.exists()) {
                ticketDatabaseReference.get().addOnSuccessListener { ticketSnapshot ->
                    if (ticketSnapshot.exists()) {
                        for (data in dataEvents.children) {
                            val event = data.getValue(EventModelData::class.java)
                            val eventDateParsed = try {
                                dateFormat.parse(event?.eventDate ?: "")
                            } catch (e: Exception) {
                                null
                            }

                            if (event != null && eventDateParsed != null && eventDateParsed.after(currentDate)) {
                                for (ticketDataSnapshot in ticketSnapshot.children) {
                                    val ticketData = ticketDataSnapshot.getValue(TicketModelData::class.java)
                                    if (ticketData != null && ticketData.eventId == event.eventId) {
                                        events.add(event)
                                        ticket.add(ticketData)
                                        break
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }.addOnFailureListener { error ->
            Log.e("Firebase", "Failed to fetch events", error)
        }
    }

    fun onClick(event: EventModelData, ticket: TicketModelData){
        val intent = Intent(requireActivity(),AttendeesEventDetail::class.java)
        intent.putExtra(AttendeesEventDetail.EXTRA_EVENT_DETAIL,event)
        intent.putExtra(AttendeesEventDetail.EXTRA_TICKET_DETAIL,ticket)
        startActivity(intent)
    }

}