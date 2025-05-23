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
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.EventOrganizerModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAdminAnalyticsReportsEventHostedBinding
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
import kotlin.math.log

class AdminAnalyticsReportsEventHosted : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAnalyticsReportsEventHostedBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminAnalyticsReportsEventHostedBinding.inflate(layoutInflater)
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
            downloadAsPDF(binding.analyticReportBarChartEventHosted,"Event_Hosted_Pdf")
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
        val spinner = binding.spinnerTimePeriodEventHosted
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
                        binding.buttonLinearLayoutEventHosted.visibility = View.GONE
                        endDate = LocalDate.now()
                        startDate = endDate?.minusWeeks(1)
                        loadDataForAdmin(startDate,endDate)
                    }
                    "Monthly" -> {
                        binding.buttonLinearLayoutEventHosted.visibility = View.GONE
                        endDate = LocalDate.now()
                        startDate = endDate?.minusMonths(1)
                        loadDataForAdmin(startDate,endDate)
                    }
                    "Custom range" -> {
                        binding.buttonLinearLayoutEventHosted.visibility = View.VISIBLE
                        val today = Calendar.getInstance()

                        val startDateButton = binding.startDateButtonEventHosted
                        val endDateButton = binding.endDateButtonEventHosted
                        val generateDataButton = binding.generateDataButtonEventHosted

                        startDateButton.setOnClickListener{
                            val datePickerStartDate = DatePickerDialog(this@AdminAnalyticsReportsEventHosted,
                                {_,year,month,day->
                                    startDate = LocalDate.of(year, month+1,day)
                                    startDateButton.text = String.format("%d-%02d-%02d", year, month+1,day)
                                },today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH))
                            datePickerStartDate.show()
                        }

                        endDateButton.setOnClickListener {
                            val datePickerEndDate = DatePickerDialog(this@AdminAnalyticsReportsEventHosted,
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
                                Toast.makeText(this@AdminAnalyticsReportsEventHosted, "Please select both dates", Toast.LENGTH_SHORT).show()
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

        val eventCountPerLocation = mutableMapOf<String,Int>()

        eventRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val event = data.getValue(EventModelData::class.java) ?: continue
                        val eventName = event.eventName ?: continue
                        val eventDateStr = event.eventDate ?: continue

                        try {
                            val eventDate = LocalDate.parse(eventDateStr,formatter)

                            if (eventDate in startDate..endDate){
                                eventCountPerLocation[eventName] =
                                    eventCountPerLocation.getOrDefault(eventName,0) + 1
                            }
                        }catch (e: Exception){
                            Toast.makeText(this, "error $e", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showBarChart(eventCountPerLocation)
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
        val resultLayout = binding.resultLinearLayoutEventHosted
        val xText = binding.xTextViewEventHosted
        xText.text = ""
        val yText = binding.yTextViewEventHosted

        val barChart = binding.analyticReportBarChartEventHosted

        if (data.isEmpty()){
            Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show()
            barChart.description.text = "Data is empty"
        }else{
            barChart.visibility = View.VISIBLE
            for ((eventName,count) in data){
                entries.add(BarEntry(index.toFloat(),count.toFloat()))
                labels.add(eventName)
                index++
            }
        }

        val dataSet = BarDataSet(entries,"Events Hosted Data")
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS.toList())

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f
        barChart.data = barData
        barChart.description.text = "Events Hosted"
        barChart.setFitBars(true)
        barChart.invalidate()


        barChart.setOnChartValueSelectedListener(object :OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val clickedEventName = labels[e.x.toInt()]

                    resultLayout.visibility = View.VISIBLE

                    xText.text = "Event name: ${clickedEventName}"
                    yText.text = "Total event Hosted: ${data.size} event"
                }
            }

            override fun onNothingSelected() {
                resultLayout.visibility = View.GONE
            }

        })

    }

}