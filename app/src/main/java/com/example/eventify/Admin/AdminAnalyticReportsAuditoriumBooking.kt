package com.example.eventify.Admin

import android.app.DatePickerDialog
import android.content.ContentValues
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAdminAnalyticReportsAuditoriumBookingBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar

class AdminAnalyticReportsAuditoriumBooking : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAnalyticReportsAuditoriumBookingBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminAnalyticReportsAuditoriumBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeUI()
        initializeListener()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initializeListener() {

        binding.PDFButton.setOnClickListener{
            downloadAsPDF(binding.analyticReportBarChartAuditoriumBooking,"Auditorium_booking_PDF")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadAsPDF(view: View, fileName:String) {
        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height,1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        view.draw(canvas)

        pdfDocument.finishPage(page)

        val resolver = this.contentResolver
        val contentValue = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValue)

        if (uri != null){
            resolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(this, "PDF save to download", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
        }
        pdfDocument.close()
    }

    private fun initializeUI() {
        val spinner = binding.spinnerTimePeriodAuditoriumBooking
        var startDate: LocalDate? = null
        var endDate: LocalDate? = null

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position).toString()
                when(selected){
                    "Weekly" -> {
                        binding.buttonLinearLayoutAuditoriumBooking.visibility = View.GONE
                        endDate = LocalDate.now()
                        startDate = endDate?.minusWeeks(1)
                        loadDataForAdmin(startDate,endDate)
                    }
                    "Monthly" -> {
                        binding.buttonLinearLayoutAuditoriumBooking.visibility = View.GONE
                        endDate = LocalDate.now()
                        startDate = endDate?.minusMonths(1)
                        loadDataForAdmin(startDate,endDate)
                    }
                    "Custom range" -> {
                        binding.buttonLinearLayoutAuditoriumBooking.visibility = View.VISIBLE
                        val today = Calendar.getInstance()

                        val startDateButton = binding.startDateButtonAuditoriumBooking
                        val endDateButton = binding.endDateButtonAuditoriumBooking
                        val generateDataButton = binding.generateDataButtonAuditoriumBooking

                        startDateButton.setOnClickListener{
                            val datePickerStartDate = DatePickerDialog(this@AdminAnalyticReportsAuditoriumBooking,
                                {_,year,month,day->
                                    startDate = LocalDate.of(year, month+1,day)
                                    startDateButton.text = String.format("%d-%02d-%02d", year, month+1,day)
                                },today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH))
                            datePickerStartDate.show()
                        }

                        endDateButton.setOnClickListener {
                            val datePickerEndDate = DatePickerDialog(this@AdminAnalyticReportsAuditoriumBooking,
                                {_,year2,month2,day2 ->
                                    endDate = LocalDate.of(year2,month2+1,day2)
                                    endDateButton.text = String.format("%d-%02d-%02d", year2,month2+1,day2)
                                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH))

                            datePickerEndDate.datePicker.minDate = startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L
                            datePickerEndDate.show()
                        }

                        generateDataButton.setOnClickListener{
                            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val startDateText = startDateButton.text.toString()
                            val endDateText = endDateButton.text.toString()

                            var startDateButtonDate: LocalDate?
                            var endDateButtonDate: LocalDate?

                            try {
                                startDateButtonDate = LocalDate.parse(startDateText,dateFormat)
                                endDateButtonDate = LocalDate.parse(endDateText,dateFormat)

                            }catch (e: DateTimeParseException){
                                startDateButtonDate = null
                                endDateButtonDate = null
                            }
                            if (startDateButtonDate != null && endDateButtonDate != null) {
                                loadDataForAdmin(startDateButtonDate, endDateButtonDate)
                            } else {
                                Toast.makeText(this@AdminAnalyticReportsAuditoriumBooking, "Please select both dates", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
    }

    private fun loadDataForAdmin(startDate: LocalDate?, endDate: LocalDate?){
        if (startDate == null || endDate == null) return

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val eventRef = FirebaseDatabase.getInstance().getReference("events")

        val auditorium = mutableMapOf<String,Int>()

        eventRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val event = data.getValue(EventModelData::class.java) ?: continue
                        val dateStr = event.eventDate ?: continue
                        val location = event.eventLocation ?: continue

                        try {
                            val eventDate = LocalDate.parse(dateStr,formatter)

                            if (eventDate in startDate..endDate){
                                auditorium[location] =
                                    auditorium.getOrDefault(location,0) + 1
                            }
                        }catch (e: Exception){
                            Toast.makeText(this, "error $e", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showBarChart(auditorium)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showBarChart(data: Map<String,Int>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var index = 0
        val resultLayout = binding.resultLinearLayoutAuditoriumBooking
        val xText = binding.xTextViewAuditoriumBooking
        val yText = binding.yTextViewAuditoriumBooking

        val barChart = binding.analyticReportBarChartAuditoriumBooking

        if (data.isEmpty()){
            Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show()
            barChart.description.text = "Data is empty"
        }else{
            barChart.visibility = View.VISIBLE
            for ((auditorium,count) in data){
                entries.add(BarEntry(index.toFloat(),count.toFloat()))
                if (auditorium !in labels){
                    labels.add(auditorium)
                }
                index++
            }
        }


        val dataSet = BarDataSet(entries,"Auditorium Booking")
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS.toList())

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f
        barChart.data = barData
        barChart.description.text = "Auditorium Booking Data"
        barChart.setFitBars(true)
        barChart.invalidate()

        barChart.setOnChartValueSelectedListener(object :OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val clickedAuditorium = labels[e.x.toInt()]
                    val clickedBooking = e.y.toInt()

                    resultLayout.visibility = View.VISIBLE

                    xText.text = "Auditorium at: ${clickedAuditorium}"
                    yText.text = "Total Bookings: $clickedBooking"
                }
            }

            override fun onNothingSelected() {
                resultLayout.visibility = View.GONE
            }

        })

    }
}