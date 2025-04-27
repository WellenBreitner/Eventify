package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventify.ModelData.EventOrganizerModelData
import com.example.eventify.R
import com.example.eventify.adminAdapter.AdminAdapter
import com.example.eventify.databinding.ActivityAdminDashboardBinding
import com.google.firebase.Firebase
import com.google.firebase.database.database

class AdminDashboard : AppCompatActivity() {
    private val eventOrgList = ArrayList<EventOrganizerModelData>()
    private lateinit var binding : ActivityAdminDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.adminDashboardRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AdminAdapter(eventOrgList)
        binding.adminDashboardRecyclerView.adapter = adapter

        getEventOrgList()


        binding.registerEOButton.setOnClickListener{
            val intent = Intent(this,RegisterActivityEO::class.java)
            startActivity(intent)
            finish()
        }
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
                binding.adminDashboardRecyclerView.adapter?.notifyDataSetChanged()
            }
        }.addOnFailureListener { error ->
            Log.e("Firebase", "Failed to fetch events", error)
        }
    }
}