package com.example.eventify.eventOrganizer

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityEventDetailBinding
import com.example.eventify.databinding.ActivityTicketSetupPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.util.Calendar
import kotlin.math.log

class TicketSetupPage : AppCompatActivity() {
    private lateinit var binding: ActivityTicketSetupPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val selectedRows = mutableListOf<String>()
    private lateinit var getEventID: String
    private lateinit var assignSeatRow: ArrayList<String>
    private lateinit var checkBoxes : List<CheckBox>
    private lateinit var unavailableSeats: ArrayList<String>
    private lateinit var seats: ArrayList<String>
    private var totalSeat = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTicketSetupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        initializeUI()
        initializeListeners()
    }

    private fun initializeUI() {
        assignSeatRow = ArrayList()
        seats = ArrayList()
        checkBoxes = listOf(
            findViewById<CheckBox>(R.id.checkboxA),
            findViewById<CheckBox>(R.id.checkboxB),
            findViewById<CheckBox>(R.id.checkboxC),
            findViewById<CheckBox>(R.id.checkboxD),
            findViewById<CheckBox>(R.id.checkboxE),
            findViewById<CheckBox>(R.id.checkboxF),
            findViewById<CheckBox>(R.id.checkboxG),
            findViewById<CheckBox>(R.id.checkboxH),
            findViewById<CheckBox>(R.id.checkboxJ),
            findViewById<CheckBox>(R.id.checkboxK),
            findViewById<CheckBox>(R.id.checkboxL),
            findViewById<CheckBox>(R.id.checkboxAA),
            findViewById<CheckBox>(R.id.checkboxBB),
            findViewById<CheckBox>(R.id.checkboxCC),
            findViewById<CheckBox>(R.id.checkboxDD),
            findViewById<CheckBox>(R.id.checkboxEE),
        )

        unavailableSeats = arrayListOf(
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


        val getIntentData = intent.getStringExtra("eventIDForTicketSetup")
        getEventID = getIntentData.toString()

        getSeatData()
    }

    private fun getSeatData() {
        assignSeatRow.clear()
        val seatRef = FirebaseDatabase.getInstance().getReference("ticket")
        seatRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                for (data in snapshot.children){
                    val seat = data.getValue(TicketModelData::class.java)
                    if (seat != null && seat.eventId == getEventID){
                        seat.assignSeat?.let { assignSeatRow.addAll(it) }
                    }
                }
                for (checkBox in checkBoxes) {
                    for (seat in assignSeatRow){
                        if (checkBox.text.equals(seat)){
                            checkBox.isEnabled = false
                        }
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching data from database", Toast.LENGTH_SHORT).show()
        }
    }



    private fun initializeListeners() {
        // Date Picker
        binding.expiredDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val dateString = String.format("%d-%02d-%02d",selectedYear, selectedMonth + 1, selectedDay)
                    binding.expiredDatePicker.text = dateString
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        // Checkbox logic
        for (checkBox in checkBoxes) {
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                val text = buttonView.text.toString()

                if (isChecked) {
                    selectedRows.add(text)
                    val matchedSeats = unavailableSeats.filter { it.matches(Regex("^$text\\d+$")) }
                    seats.addAll(matchedSeats)
                } else {
                    selectedRows.remove(text)
                    seats.removeAll { it.matches(Regex("^$text\\d+$")) }
                }


                val numberOfSeat = selectedRows.size * 50
                totalSeat = numberOfSeat - seats.size

                Log.d("TAG", "selectedRows: $selectedRows")
                Log.d("TAG", "number of seat: $numberOfSeat")
                Log.d("TAG", "total seat: $totalSeat")
            }
        }


        // Cancel button
        binding.cancelAddEventButton.setOnClickListener {
            finish()
        }

        // Add button with validation
        binding.addTicketButton.setOnClickListener {
            val ticketType = binding.ticketTypeEditText.text.toString().trim()
            val ticketPrice = binding.ticketPriceEditText.text.toString().trim()
            val maxWaitlist = binding.maxWaitlistEditText.text.toString().trim()
            val promoCode = binding.promotionCodeEditText.text.toString().trim()
            val promoDiscountText = binding.promotionDiscountEditText.text.toString().trim()
            val promoDiscount = promoDiscountText.toIntOrNull()
            val expiredDate = binding.expiredDatePicker.text.toString().trim()
            val date = if (expiredDate == "Set date") "0" else expiredDate

            if (ticketType.isEmpty()) {
                binding.ticketTypeEditText.error = "Ticket type is required"
                return@setOnClickListener
            }

            if (ticketPrice.isEmpty()) {
                binding.ticketPriceEditText.error = "Ticket price is required"
                return@setOnClickListener
            }

            if (maxWaitlist.isEmpty()) {
                binding.maxWaitlistEditText.error = "Max waitlist is required"
                return@setOnClickListener
            }

            if (selectedRows.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat row", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isAnyPromoFieldFilled = promoCode.isNotEmpty() || promoDiscountText.isNotEmpty() || expiredDate != "Set date"
            val isAllPromoFieldsFilled = promoCode.isNotEmpty() && promoDiscountText.isNotEmpty() && expiredDate != "Set date"

            if (isAnyPromoFieldFilled && !isAllPromoFieldsFilled) {
                Toast.makeText(this, "Please complete all promotion fields (Code, Discount, and Expiry Date).", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (promoDiscountText.isNotEmpty() && promoDiscount == null) {
                binding.promotionDiscountEditText.error = "Discount must be a valid number"
                return@setOnClickListener
            }

            val ticketRef = FirebaseDatabase.getInstance().getReference("ticket")
            val getID = ticketRef.push().key
            val ticketData = TicketModelData(
                getID,
                getEventID,
                ticketType,
                ticketPrice.toInt(),
                totalSeat,
                null,
                null,
                promoCode.uppercase(),
                promoDiscount ?: 0,
                date,
                maxWaitlist.toInt(),
                selectedRows
            )

            ticketRef.child(getID.toString()).setValue(ticketData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ticket added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add ticket", Toast.LENGTH_SHORT).show()
                }
        }
    }
}