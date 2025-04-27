package com.example.eventify.attendees

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAttendeesPaymentInformationBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendeesPaymentInformation : AppCompatActivity() , PaymentResultListener {
    private var getPaymentInformation: BookingModelData? = null
    private lateinit var binding: ActivityAttendeesPaymentInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAttendeesPaymentInformationBinding.inflate(layoutInflater)
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

    private fun initializeUI() {
        getPaymentInformation = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("payment_information", BookingModelData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("payment_information")
        }

        binding.paymentInformationTicketType.text = getPaymentInformation?.ticketType.toString()
        binding.paymentInformationSelectedSeat.text = getPaymentInformation?.selectedSeat?.joinToString(",")
        binding.paymentInformationNumberOfTicket.text = getPaymentInformation?.numberOfTicket
        binding.paymentInformationPriceForEachTicket.text =
            "$${getPaymentInformation?.priceForEachTicket} x ${getPaymentInformation?.numberOfTicket}"
        binding.paymentInformationTotalPrice.text = "$${getPaymentInformation?.totalPrice?.toDouble().toString()}"
    }

    private fun initializeListener() {
        binding.paymentInformationPayButton.setOnClickListener {
            makePayment()
        }

        binding.paymentInformationPromotionCodeButton.setOnClickListener {
            applyPromotionCode()
        }
    }

    private fun applyPromotionCode() {
        val calender = Calendar.getInstance()
        val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if (binding.paymentInformationPromotionCodeEditText.text?.isEmpty() == true) {
            binding.paymentInformationPromotionCodeEditText.error = "Promotion code can't be empty"
            binding.paymentInformationPromotionCodeEditText.requestFocus()
        } else {
            val getPromotionCode = binding.paymentInformationPromotionCodeEditText.text.toString()
            val eventRef = Firebase.database.getReference("events")

            eventRef.get().addOnSuccessListener { dataEvent ->
                if (dataEvent.exists()) {
                    for (data in dataEvent.children) {
                        val event = data.getValue(EventModelData::class.java)
                        val ticket = event?.ticket

                        if (event != null && ticket != null && getPaymentInformation?.eventID == event.eventId) {
                            val expiryDate = try {
                                simpleFormat.parse(ticket.expiryDate ?: "")
                            } catch (e: Exception) {
                                null
                            }

                            if (ticket.promotionCode == getPromotionCode && expiryDate?.after(calender.time) == true) {
                                val getDiscount = (ticket.discount ?: 0)
                                val originalPrice = (getPaymentInformation?.priceForEachTicket.toString().toDouble() * getPaymentInformation?.numberOfTicket.toString().toInt())
                                binding.paymentInformationDiscount.text = "${ticket.discount.toString()}%"
                                val newTotalPrice = originalPrice - (originalPrice * getDiscount / 100)
                                binding.paymentInformationTotalPrice.text = "$newTotalPrice"
                                getPaymentInformation?.totalPrice = newTotalPrice

                                binding.paymentInformationPromotionCodeEditText.isEnabled = false
                                binding.paymentInformationPromotionCodeButton.isEnabled = false
                                Toast.makeText(this, "Valid Promotion Code", Toast.LENGTH_SHORT).show()
                            }else{
                                binding.paymentInformationPromotionCodeEditText.error = "Invalid Promotion Code"
                                binding.paymentInformationPromotionCodeEditText.requestFocus()
                            }
                        }
                    }
                }
            }.addOnFailureListener{
                Log.e("Firebase", "Error fetching data", it)
            }
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
            options.put("amount",(getPaymentInformation?.totalPrice ?: 0).toDouble() * 100.0);

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
            Toast.makeText(this, "payment successfully", Toast.LENGTH_SHORT).show()
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




