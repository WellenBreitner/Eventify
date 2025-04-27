package com.example.eventify.eventOrganizer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.eoAdapter.EOAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.database


class EventOrganizerHome : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: ArrayList<EventModelData>
    private lateinit var view: View
    private lateinit var addClassButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_eo_home, container, false)

        eventList = arrayListOf()

        recyclerView = view.findViewById(R.id.EORVEventList)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = EOAdapter(eventList)
        recyclerView.setHasFixedSize(true)

        getEventData()

        addClassButton = view.findViewById(R.id.adminAddClassButton)

        addClassButton.setOnClickListener {
            val intent = Intent(requireActivity(),EOAddEvent::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun getEventData() {
        eventList.clear()
        val eventRef = Firebase.database.getReference("events")

        eventRef.get().addOnSuccessListener { dataEvent ->
            if(dataEvent.exists()){
                for (data in dataEvent.children){
                    val event = data.getValue(EventModelData::class.java)
                    if (event!=null){
                        eventList.add(0,
                            EventModelData(
                                event.eventId,
                                event.eventName,
                                event.eventDescription,
                                event.eventDate,
                                event.eventLocation,
                                event.organizerId,
                                TicketModelData(
                                    event.ticket?.ticketId,
                                    event.ticket?.eventId,
                                    event.ticket?.ticketType,
                                    event.ticket?.ticketRemaining,
                                    event.ticket?.ticketAvailable,
                                    event.ticket?.ticketLimit
                                ),
                                null)
                        )
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }

        }
    }
}