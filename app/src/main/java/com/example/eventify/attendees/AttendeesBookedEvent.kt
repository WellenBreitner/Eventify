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
import com.example.eventify.ModelData.BookedSeatModelData
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.attendeesAdapter.AttendeesEventAdapter
import com.example.eventify.attendeesAdapter.AttendeesEventBookedAdapter
import com.example.eventify.attendeesAdapter.SeatAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.util.ArrayList

class AttendeesBookedEvent : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var events: ArrayList<BookingModelData>
    private lateinit var view: View
    private lateinit var eventDatabaseReference: DatabaseReference
    private lateinit var adapter: AttendeesEventBookedAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_attendees_booked_event, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        initializeUI()
        initializeListener()

        return view
    }

    private fun initializeListener() {
        adapter.setOnEventBookedClick(object : AttendeesEventBookedAdapter.eventBookedClick{
            override fun onClick(dataBooking: BookingModelData) {
                val intent = Intent(requireActivity(),AttendeesEventBookedDetail::class.java)
                intent.putExtra("event_booked_detail",dataBooking)
                startActivity(intent)
            }
        })
    }

    private fun initializeUI() {
        events = arrayListOf()

        recyclerView = view.findViewById(R.id.attendeesEventBookedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = AttendeesEventBookedAdapter(events)
        recyclerView.adapter = adapter

        getEventBookingFromUserDataBase()
    }

    private fun getEventBookingFromUserDataBase() {
            events.clear()
            val userID = firebaseAuth.currentUser?.uid

            eventDatabaseReference = Firebase.database.getReference("bookings")

            eventDatabaseReference.get()
                .addOnSuccessListener { dataBookings ->
                    if (dataBookings.exists()) {
                        for (data in dataBookings.children) {
                            val bookingEvent = data.getValue(BookingModelData::class.java)
                            if (bookingEvent != null && bookingEvent.userId == userID) {
                                events.add(0,
                                    BookingModelData(
                                    bookingEvent.bookingId,
                                        bookingEvent.userId,
                                        bookingEvent.userEmail,
                                        bookingEvent.eventID,
                                        bookingEvent.eventName,
                                        bookingEvent.eventDate,
                                        bookingEvent.eventLocation,
                                        bookingEvent.ticketType,
                                        bookingEvent.numberOfTicket,
                                        bookingEvent.priceForEachTicket,
                                        bookingEvent.totalPrice,
                                        bookingEvent.selectedSeat
                                )
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
        }
}