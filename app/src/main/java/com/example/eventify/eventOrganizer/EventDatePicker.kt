package com.example.eventify.eventOrganizer

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.DatePicker
import android.widget.TextView
import java.util.Calendar
import java.util.Locale

class EventDatePicker(private val datePicker: TextView) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%d", day, month + 1, year)
        datePicker.text = formattedDate
    }
}