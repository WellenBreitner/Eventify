package com.example.eventify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.attendeesAdapter.AttendeesEventAdapter

class AttendeesEventListPage : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var events: ArrayList<EventModelData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendees_event_list_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        events = ArrayList()
        //this is dummy data it will deleted after having database
        events.add(EventModelData(null, "1", "Beach Cleanup", "A community-driven beach cleanup event to protect marine life.", "2025-04-05 08:00", "org1"))
        events.add(EventModelData(null, "2", "Turtle Nesting Awareness", "Educational event to raise awareness about turtle nesting.", "2025-04-10 10:30", "org1"))
        events.add(EventModelData(null, "3", "Fundraising Gala for Turtle Conservation", "A formal gala to raise funds for turtle conservation efforts.", "2025-04-15 19:00", "org2"))
        events.add(EventModelData(null, "4", "Turtle Conservation Webinar", "An online webinar discussing strategies to protect turtles.", "2025-04-20 14:00", "org3"))
        events.add(EventModelData(null, "5", "Marine Life Protection Campaign", "A series of workshops on protecting marine ecosystems.", "2025-04-25 09:00", "org4"))
        events.add(EventModelData(null, "6", "Underwater Cleanup Dive", "Join us for a scuba dive cleanup event to remove debris from the ocean floor.", "2025-05-01 07:30", "org5"))
        events.add(EventModelData(null, "7", "Turtle Rehabilitation Program", "An event dedicated to learning about turtle rehabilitation and rescue.", "2025-05-05 13:00", "org1"))
        events.add(EventModelData(null, "8", "Eco-friendly Living Workshop", "A workshop focused on living sustainably to protect marine environments.", "2025-05-10 11:00", "org2"))
        events.add(EventModelData(null, "9", "Turtle Migration Awareness Walk", "A walk to raise awareness about the migration routes of turtles.", "2025-05-15 16:30", "org3"))
        events.add(EventModelData(null, "10", "Sea Turtle Festival", "A celebration of sea turtle conservation efforts with various activities.", "2025-05-20 18:00", "org4"))


        recyclerView = findViewById(R.id.attendeesEventListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AttendeesEventAdapter(events)
    }
}