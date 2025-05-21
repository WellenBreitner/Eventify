package com.example.eventify.eventOrganizer

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class EventDatePicker(private val datePicker: TextView) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val disabledDates = mutableListOf<LocalDate>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        getDisabledDatesFromRealtimeDatabase { dates ->
            disabledDates.clear()
            disabledDates.addAll(dates)

            val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)

            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_MONTH, 1)
            datePickerDialog.datePicker.minDate = tomorrow.timeInMillis

            val datePicker = datePickerDialog.datePicker
            val nextValidDate = getNextValidDate()
            datePicker.updateDate(nextValidDate.year, nextValidDate.monthValue - 1, nextValidDate.dayOfMonth)

            datePicker.setOnDateChangedListener { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                if (disabledDates.contains(selectedDate)) {
                    val validDate = getNextValidDate()
                    datePicker.updateDate(validDate.year, validDate.monthValue - 1, validDate.dayOfMonth)
                    Toast.makeText(requireContext(), "This Date Is Not Available, Please Choose Another Date", Toast.LENGTH_SHORT).show()
                }
            }

            datePickerDialog.show()
        }

        return super.onCreateDialog(savedInstanceState)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNextValidDate(): LocalDate {
        var nextValidDate = LocalDate.now().plusDays(1)
        while (disabledDates.contains(nextValidDate)) {
            nextValidDate = nextValidDate.plusDays(1)
        }
        return nextValidDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDisabledDatesFromRealtimeDatabase(onComplete: (List<LocalDate>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("events")

        myRef.get().addOnSuccessListener { snapshot ->
            val dates = mutableListOf<LocalDate>()
            for (child in snapshot.children) {
                val eventDate = child.child("eventDate").getValue(String::class.java) ?: continue
                try {
                    val parsedDate = LocalDate.parse(eventDate.substring(0, 10))
                    dates.add(parsedDate)
                } catch (e: Exception) {
                    null
                }
            }
            onComplete(dates)
        }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching dates: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val selectedDate = LocalDate.of(year, month + 1, day)

        if (disabledDates.contains(selectedDate)) {
            Toast.makeText(requireContext(), "This Date Already Taken By Another Event", Toast.LENGTH_SHORT).show()
        } else {
            val formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day)
            datePicker.text = formattedDate
        }
        dismiss()
    }
}

