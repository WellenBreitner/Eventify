package com.example.eventify.attendees

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.SeatModelData
import com.example.eventify.R
import com.example.eventify.attendeesAdapter.SeatAdapter
import com.example.eventify.attendeesViewModel.TicketTypeViewModel
import com.example.eventify.databinding.ActivityAttendeesDashboardBinding
import com.example.eventify.databinding.ActivityAttendeesPurchaseTicketBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlin.math.log

class AttendeesPurchaseTicket : AppCompatActivity() {

    private lateinit var ticketTypeViewModel: TicketTypeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketTypeTextView: TextView
    private lateinit var binding: ActivityAttendeesPurchaseTicketBinding

    private lateinit var getEventID: String
    private lateinit var getPriceForEach: String
    private var getEventInformation: EventModelData? = null

    private val selectedSeat = ArrayList<String>()
    private var unavailableSeats = hashSetOf<String>()
    private var bookedSeat = hashSetOf<String>()
    private val seats = mutableListOf<SeatModelData>()
    private val rows = listOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L","AA","BB","CC","DD","EE")
    private val cols = 50
    private var isTicketTypeSelected = false
    private var isRecyclerInitialized = false
    private var price: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAttendeesPurchaseTicketBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initializeUI()
        initializeListener()
    }

    private fun initializeListener() {
        binding.attendeesSelectTicketType.setOnClickListener {
            attendeesSelectTypeOnClick()
        }

        binding.attendeesBookingButton.setOnClickListener {
            attendeesBookingButtonOnClick()
        }
    }

    private fun initializeUI() {
        ticketTypeViewModel = ViewModelProvider(this)[TicketTypeViewModel::class.java]
        recyclerView = findViewById(R.id.eventSeatSelectionForPurchasesTicket)

        getEventInformation = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra("event_id",EventModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event_id")
        }

        getEventID = getEventInformation?.eventId.toString()
        ticketTypeViewModel.setEventID(getEventID)
        ticketTypeViewModel.getTicketTypeData.observe(this){data ->
            binding.ticketTypeAttendeesChoose.text = data
            isTicketTypeSelected = data != "Not selected"

            if (isTicketTypeSelected) {
                showNestedScrollWithAnimation(true)
            } else {
                showNestedScrollWithAnimation(false)
            }
        }

        unavailableSeats = hashSetOf(
            "A9","A10","A11","A12","A13","A14","A34","A35","A44","A45","A46","A47","A48","A49","A50",
            "B11","B12","B13","B14","B34","B35","B45","B46","B47","B48","B49","B50",
            "C12","C13","C14","C34","C35","C47","C48","C49","C50",
            "D13","D14","D35","D48","D49","D50",
            "E13","E14","E32","E33","E34","E35","E48","E49","E50",
            "F13","F14","F33","F34","F35","F48","F49","F50",
            "G13","G14","G32","G33","G34","G35","G48","G49","G50",
            "H12","H13","H14","H33","H34","H35","H36","H47","H48","H49","H50",
            "J11","J12","J13","J14","J30","J31","J32","J33","J34","J35","J46","J46","J47","J48","J49","J50",
            "K9","K10","K11","K12","K13","K14","K31","K32","K33","K34","K35","K44","K45","K46","K47","K48","K49","K50",
            "L6","L7","L8","L9","L10","L11","L12","L13","L14","L15","L16","L17","L18","L19",
            "L20","L21","L22","L23","L24","L25","L26","L27","L28","L29",
            "L30","L31","L32","L33","L34","L35",
            "L41","L42","L43","L44","L45","L46","L47","L48","L49","L50",
            "AA14","BB14","CC14","DD14","DD36","DD50",
            "EE13","EE14","EE15","EE16","EE17","EE18","EE19",
            "EE20","EE21","EE22","EE23","EE24","EE25","EE26","EE27","EE28","EE29",
            "EE30","EE31","EE32","EE33","EE34","EE35","EE36","EE49","EE50"
            )
    }

    private fun seatSetup(){
        for(row in rows){
            for (number in cols downTo 1){
                seats.add(SeatModelData(row + number,row,number,false))
            }
        }
    }

    private fun showNestedScrollWithAnimation(show: Boolean) {
        TransitionManager.beginDelayedTransition(binding.horizontalScrollViewForRecyclerView.parent as ViewGroup, AutoTransition())
        TransitionManager.beginDelayedTransition(binding.seatInformation.parent as ViewGroup, AutoTransition())
        binding.horizontalScrollViewForRecyclerView.visibility = if (show) View.VISIBLE else View.GONE
        binding.seatInformation.visibility = if (show) View.VISIBLE else View.GONE
        if (show && !isRecyclerInitialized) {
            getSelectedSeat()
        }
    }

    private fun initializeRecycleViewAndSeatSelectionFeature(){
            recyclerView.layoutManager = GridLayoutManager(this,cols)
            recyclerView.setHasFixedSize(true)
            seatSetup()
            val adapter = SeatAdapter(seats,unavailableSeats,bookedSeat)
            recyclerView.adapter = adapter

            adapter.setSeatOnClick(object:SeatAdapter.seatOnClick{
                override fun onClick(seatModelData: SeatModelData) {

                    if (!seatModelData.isSelected){
                        Toast.makeText(this@AttendeesPurchaseTicket, "${seatModelData.label} selected", Toast.LENGTH_SHORT).show()
                        seatModelData.isSelected = true
                        selectedSeat.add(seatModelData.label)

                    }else{
                        Toast.makeText(this@AttendeesPurchaseTicket, "${seatModelData.label} unselected", Toast.LENGTH_SHORT).show()
                        seatModelData.isSelected = false
                        selectedSeat.remove(seatModelData.label)
                    }
                    binding.numberOfPurchaseTicket.text = selectedSeat.size.toString()

                    ticketTypeViewModel.getTicketPriceData.observe(this@AttendeesPurchaseTicket){data ->
                        getPriceForEach = data
                        price = (data.toDouble() * selectedSeat.size)
                        val convertCurrency = "${ (data.toInt() * selectedSeat.size).toDouble()}"
                        binding.attendeesTicketTotalPrice.text = "$${convertCurrency}"
                    }

                    binding.attendeesSelectSeat.text = ""
                    if (selectedSeat.size == 0 ) {
                        binding.attendeesSelectSeat.text = "Not selected"
                    }else{
                        binding.attendeesSelectSeat.text = selectedSeat.joinToString(",")
                    }
                }
            })
    }

    private fun attendeesSelectTypeOnClick() {
        val fragment = AttendeesTicketType()
        val fragmentManager = supportFragmentManager
        fragment.show(fragmentManager,"Attendees Ticket Type Dialog Fragment")
    }

    private fun attendeesBookingButtonOnClick() {
        val numberTicket = binding.numberOfPurchaseTicket.text.toString()
        if (isTicketTypeSelected){
            if(numberTicket.toInt() > 0){
                val intent = Intent(this,AttendeesPaymentInformation::class.java)
                intent.putExtra("payment_information",
                    BookingModelData(
                        null,
                        null,
                        null,
                        null,
                        getEventID,
                        getEventInformation?.eventName.toString(),
                        getEventInformation?.eventDate.toString(),
                        getEventInformation?.eventLocation.toString(),
                        binding.ticketTypeAttendeesChoose.text.toString(),
                        numberTicket,
                        getPriceForEach,
                        price,
                        selectedSeat
                    )
                )
                startActivity(intent)
            }else{
                Toast.makeText(this, "You required to select seat", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "You required to select ticket type first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedSeat(){
        val bookingRef = Firebase.database.getReference("bookings")
        bookingRef.get().addOnSuccessListener { eventid ->
            if (eventid.exists()){
                for (id in eventid.children){
                    val event_id = id.getValue(BookingModelData::class.java)
                    if (event_id?.eventID == getEventID){
                        event_id.selectedSeat.let { seat ->
                            event_id.selectedSeat.forEach { seat ->
                                bookedSeat.add(seat.trim().uppercase())
                            }
                        }
                    }
                }
            }
            initializeRecycleViewAndSeatSelectionFeature()
            isRecyclerInitialized = true
        }
    }
}