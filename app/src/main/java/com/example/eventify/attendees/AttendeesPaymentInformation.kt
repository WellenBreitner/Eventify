package com.example.eventify.attendees

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class AttendeesPaymentInformation : AppCompatActivity() , PaymentResultListener {
    //promotioncode
    private lateinit var ticketTypeTextView: TextView
    private lateinit var selectedSeatTextView: TextView
    private lateinit var numberOfTicketTextView: TextView
    private lateinit var priceForEachTicketTextView: TextView
    private lateinit var discountTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var payButton: MaterialButton
    private lateinit var promotionCode: MaterialButton
    private var getPaymentInformation: BookingModelData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendees_payment_information)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
        initializeListener()
    }

    private fun initializeUI() {
        ticketTypeTextView = findViewById(R.id.paymentInformationTicketType)
        selectedSeatTextView = findViewById(R.id.paymentInformationSelectedSeat)
        numberOfTicketTextView = findViewById(R.id.paymentInformationNumberOfTicket)
        priceForEachTicketTextView = findViewById(R.id.paymentInformationPriceForEachTicket)
        discountTextView = findViewById(R.id.paymentInformationDiscount)
        totalPriceTextView = findViewById(R.id.paymentInformationTotalPrice)
        payButton = findViewById(R.id.paymentInformationPayButton)

        getPaymentInformation = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("payment_information", BookingModelData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("payment_information")
        }

        ticketTypeTextView.text = getPaymentInformation?.ticketType.toString()
        selectedSeatTextView.text = getPaymentInformation?.selectedSeat?.joinToString(",")
        numberOfTicketTextView.text = getPaymentInformation?.numberOfTicket
        priceForEachTicketTextView.text =
            "${getPaymentInformation?.priceForEachTicket} x ${getPaymentInformation?.numberOfTicket}"
        totalPriceTextView.text = "$${getPaymentInformation?.totalPrice?.toDouble().toString()}"
    }

    private fun initializeListener() {
        payButton.setOnClickListener {
            makePayment()
        }
    }

    private fun makePayment() {
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Eventify")
            options.put("description","Demoing Charges")
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency","USD");
            options.put("order_id", getPaymentInformation?.bookingId);
            options.put("amount",(getPaymentInformation?.totalPrice?.times(100)).toString());

            val prefill = JSONObject()
            prefill.put("email",getPaymentInformation?.userEmail)

            options.put("prefill",prefill)
            co.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "payment successfully", Toast.LENGTH_SHORT).show()
        val bookingRef = Firebase.database.getReference("bookings")
        val getID = bookingRef.push().key
        bookingRef.child(getID.toString()).setValue(getPaymentInformation?.selectedSeat?.let {
            BookingModelData(
                getID,
                getPaymentInformation?.userId,
                getPaymentInformation?.username,
                getPaymentInformation?.userEmail,
                getPaymentInformation?.eventID,
                getPaymentInformation?.eventName,
                getPaymentInformation?.eventDate,
                getPaymentInformation?.eventLocation,
                getPaymentInformation?.ticketType,
                getPaymentInformation?.numberOfTicket,
                getPaymentInformation?.priceForEachTicket,
                getPaymentInformation?.totalPrice,
                it
            )
        }
        ).addOnSuccessListener {
            Toast.makeText(this, "Payment has been store in to database", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"Payment not save in to database",Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(this,AttendeesDashboard::class.java)
        startActivity(intent)
        finish()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "payment failed", Toast.LENGTH_SHORT).show()
        finish()
    }
}




