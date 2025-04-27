package com.example.eventify.attendees

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAttendeesEventBookedDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.database.database

class AttendeesEventBookedDetail : AppCompatActivity() {
    private lateinit var binding: ActivityAttendeesEventBookedDetailBinding
    private lateinit var getEventBooked: BookingModelData

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAttendeesEventBookedDetailBinding.inflate(layoutInflater)
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
        binding.attendeesEventBookedDetailCancelButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancel Event")
                .setMessage("Are you sure want to cancel attend event? (no refund)")
                .setPositiveButton("Yes",DialogInterface.OnClickListener { dialog,_ ->
                    val bookingsRef = Firebase.database.getReference("bookings")
                    getEventBooked.bookingId?.let { id ->
                        bookingsRef.child(id).removeValue().addOnSuccessListener {
                            Toast.makeText(this, "Event Cancel Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                            .addOnFailureListener{
                                Toast.makeText(this, "Event Delete Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                    dialog.dismiss()
                })
                .setNegativeButton("No",DialogInterface.OnClickListener{ dialog,_ ->
                    dialog.cancel()
                })
                .show()
        }
    }

    private fun initializeUI() {
        getEventBooked = if(Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra("event_booked_detail",BookingModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event_booked_detail")
        } ?: return

        binding.attendeesEventBookedDetailName.text = getEventBooked.eventName
        binding.attendeesEventBookedDetailBookingId.text = "Booking ID: ${getEventBooked?.bookingId}"
        binding.attendeesEventBookedDetailDate.text ="Event Date: ${getEventBooked?.eventDate}"
        binding.attendeesEventBookedDetailLocation.text = "Event Location: ${getEventBooked?.eventLocation}"
        binding.attendeesEventBookedDetailSeat.text = "Selected Seat: ${
            getEventBooked?.selectedSeat?.joinToString(
                ","
            )
        }"
        binding.attendeesEventBookedDetailNumberOfTicket.text = "Number Of Ticket: ${getEventBooked?.numberOfTicket}"
        binding.attendeesEventBookedDetailTicketType.text = "Ticket Type: ${getEventBooked?.ticketType}"
        binding.attendeesEventBookedDetailTotalPrice.text = "Total Price: $${getEventBooked?.totalPrice.toString()}"
    }
}