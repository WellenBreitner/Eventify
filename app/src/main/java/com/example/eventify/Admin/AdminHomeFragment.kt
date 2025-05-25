package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.Admin.AdminAnalyticsReportEO.Companion.EVENT_ORGANIZER_DATA
import com.example.eventify.ModelData.EventOrganizerModelData
import com.example.eventify.R
import com.example.eventify.adminAdapter.AdminAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.database

class AdminHomeFragment : Fragment() {
    private val eventOrgList = ArrayList<EventOrganizerModelData>()
    private lateinit var recyclerView:RecyclerView
    private lateinit var registerEOButton: Button

    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        initializeUI()
        initializeListener()
        return view
    }

    private fun initializeListener() {
        registerEOButton.setOnClickListener{
            val intent = Intent(requireActivity(),RegisterActivityEO::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun initializeUI() {
        recyclerView = view.findViewById(R.id.adminDashboardRecyclerView)
        registerEOButton = view.findViewById(R.id.registerEOButton)

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = AdminAdapter(eventOrgList)
        recyclerView.adapter = adapter


        getEventOrgList()


        adapter.setEventOrganizerOnClick(object : AdminAdapter.eventOrganizerOnCLick {
            override fun onClick(data: EventOrganizerModelData) {
                val intent = Intent(requireActivity(),AdminAnalyticsReportEO::class.java)
                intent.putExtra(EVENT_ORGANIZER_DATA,data.eventOrganizerID)
                startActivity(intent)
            }
        })


    }

    private fun getEventOrgList() {
        val eventRef = Firebase.database.getReference("event_organizers")
        eventRef.get().addOnSuccessListener {
                dataEO ->
            if (dataEO.exists()){
                for (eo in dataEO.children){
                    val eventOrg = eo.getValue(EventOrganizerModelData::class.java)
                    if(eventOrg!=null){
                        val eventOrganizer = EventOrganizerModelData(
                            eventOrg.eventOrganizerID,
                            eventOrg.fullName,
                            eventOrg.email,
                            eventOrg.phone,
                            eventOrg.organization
                        )
                        eventOrgList.add(0,eventOrganizer)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }.addOnFailureListener { error ->
            Log.e("Firebase", "Failed to fetch events", error)
        }
    }

}