package com.example.eventify.attendees

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.SeatModelData
import com.example.eventify.R
import com.example.eventify.attendeesAdapter.SeatAdapter

class AttendeesPurchaseTicket : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val seats = mutableListOf<SeatModelData>()
    private val rows = listOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L","AA","BB","CC","DD","EE")
    private val cols = 21+13+13
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendees_purchase_ticket)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initializeUI()
    }

    private fun initializeUI() {
        recyclerView = findViewById(R.id.eventSeatSelectionForPurchasesTicket)
        recyclerView.layoutManager = GridLayoutManager(this,cols)
        seatSetup()
        val adapter = SeatAdapter(seats)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        adapter.setSeatOnClick(object:SeatAdapter.seatOnClick{
            override fun onClick(seatModelData: SeatModelData) {
                if (!seatModelData.isSelected){
                    Toast.makeText(this@AttendeesPurchaseTicket, "${seatModelData.label} selected", Toast.LENGTH_SHORT).show()
                    seatModelData.isSelected = true

                }else{
                    Toast.makeText(this@AttendeesPurchaseTicket, "${seatModelData.label} unselected", Toast.LENGTH_SHORT).show()
                    seatModelData.isSelected = false
                }

            }

        })


    }

    private fun seatSetup(){
        for(row in rows){
            for (number in cols downTo 1){
                seats.add(SeatModelData(row + number,row,number,false))
            }
        }
    }
}