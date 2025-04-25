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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale


class AttendeesEventList : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var events: ArrayList<EventModelData>
    private lateinit var eventDatabaseReference: DatabaseReference
    private lateinit var adapter: AttendeesEventAdapter
    private lateinit var view : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_attendees_event_list, container, false)

        initializeUI()
        initializeListener()
        return view
    }

    private fun initializeUI() {
        events = ArrayList()
        recyclerView = view.findViewById(R.id.attendeesEventListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = AttendeesEventAdapter(events)
        recyclerView.adapter = adapter

        getDataEventForAttendees()
    }

    private fun initializeListener() {
        adapter.setOnClickEventListener(object: AttendeesEventAdapter.onClickEventListener {
            override fun onClickItem(dataEvent: EventModelData) {
                onClick(dataEvent)
            }
        })
    }

    private fun getDataEventForAttendees(){
        events.clear()

        val calender = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calender.time)

        eventDatabaseReference = Firebase.database.getReference("events")

        eventDatabaseReference.get()
            .addOnSuccessListener { dataEvents ->
                if (dataEvents.exists()) {
                    for (data in dataEvents.children) {
                        val event = data.getValue(EventModelData::class.java)
                        if (event != null && event.eventDate > formattedDate ) {
                            events.add(0,
                                EventModelData(
                                event.eventId,
                                event.eventName,
                                event.eventDescription,
                                event.eventDate,
                                event.eventLocation,
                                event.organizerId,
                                TicketModelData(
                                    event.ticket?.ticketId,
                                    event.eventId,
                                    event.ticket?.ticketType,
                                    event.ticket?.ticketRemaining,
                                    event.ticket?.ticketAvailable,
                                    event.ticket?.ticketLimit
                                )
                            )
                            )
                        }else{
                            Log.wtf("date","sudah lama ")
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Log.e("database", "Firebase error", it)
            }
    }


    fun onClick(event: EventModelData){
        val intent = Intent(requireActivity(),AttendeesEventDetail::class.java)
        intent.putExtra(AttendeesEventDetail.EXTRA_EVENT_DETAIL,event)
        startActivity(intent)
    }

}