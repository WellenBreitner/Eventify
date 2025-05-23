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
import com.example.eventify.attendees.AttendeesEventDetail
import com.example.eventify.eoAdapter.EOAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.database


class EventOrganizerHome : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: ArrayList<EventModelData>
    private lateinit var view: View
    private lateinit var addClassButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_eo_home, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        eventList = arrayListOf()

        recyclerView = view.findViewById(R.id.EORVEventList)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = EOAdapter(
            eventList,
            onCardClick = { event, ticket, total ->
                val intent = Intent(requireActivity(), EventDetail::class.java)
                intent.putExtra(EventDetail.EXTRA_EVENT_DETAIL, event)
                intent.putExtra(EventDetail.EXTRA_TICKET_DETAIL, ticket)
                intent.putExtra(EventDetail.EXTRA_TICKET_TOTAL, total)
                startActivity(intent)
            },
            onEditClick = { event ->
                val intent = Intent(requireActivity(), EventEditPage::class.java)
                intent.putExtra(EventDetail.EXTRA_EVENT_DETAIL, event)
                startActivity(intent)
            }
        )
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
        val userID = firebaseAuth.currentUser?.uid
        eventList.clear()
        val eventRef = Firebase.database.getReference("events")

        eventRef.get().addOnSuccessListener { dataEvent ->
            if(dataEvent.exists()){
                for (data in dataEvent.children){
                    val event = data.getValue(EventModelData::class.java)
                    if (event!=null && event.organizerId == userID){
                        eventList.add(0,
                            EventModelData(
                                event.eventId,
                                event.eventName,
                                event.eventDescription,
                                event.eventDate,
                                event.eventTime,
                                event.eventLocation,
                                event.organizerId,
                                null)
                        )
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }

        }
    }
    fun onClick(event: EventModelData, ticket: TicketModelData, totalOfTicket:Int){
        val intent = Intent(requireActivity(), EventDetail::class.java)
        intent.putExtra(EventDetail.EXTRA_EVENT_DETAIL,event)
        intent.putExtra(EventDetail.EXTRA_TICKET_DETAIL,ticket)
        intent.putExtra(EventDetail.EXTRA_TICKET_TOTAL,totalOfTicket)
        startActivity(intent)
    }
}