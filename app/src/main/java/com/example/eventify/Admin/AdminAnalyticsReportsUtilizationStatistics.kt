package com.example.eventify.Admin

import android.app.DatePickerDialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAdminAnalyticsReportsUtilizationStatisticsBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Calendar

class AdminAnalyticsReportsUtilizationStatistics : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAnalyticsReportsUtilizationStatisticsBinding
    private lateinit var spinnerData: ArrayList<String>
    private var utilizationAuditorium: Double = 0.0
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminAnalyticsReportsUtilizationStatisticsBinding.inflate(layoutInflater)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initializeListener() {
        binding.PDFButton.setOnClickListener{
            downloadAsPDF(binding.analyticReportPieChartUtilizationStatisticsd,"Auditorium_utilization_pdf")
        }
    }

    private fun initializeUI() {
        spinnerData = ArrayList()
        val spinner = binding.spinnerTimePeriodUtilizationStatistics
        var startDate: LocalDate? = null
        var endDate: LocalDate? = null
        var selectedAuditorium: String? = null

        initializeSpinnerAuditorium()

        binding.spinnerAuditorium.setSelection(0)
        binding.spinnerTimePeriodUtilizationStatistics.setSelection(0)

        val spinnerAuditorium = binding.spinnerAuditorium
        spinnerAuditorium.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedAuditorium = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

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
                        binding.buttonLinearLayoutUtilizationStatistics.visibility = View.GONE
                        endDate = LocalDate.now()
                        startDate = endDate?.minusWeeks(1)
                        loadDataForAdmin(selectedAuditorium,startDate,endDate)
                    }
                    "Monthly" -> {
                        binding.buttonLinearLayoutUtilizationStatistics.visibility = View.GONE
                        endDate = LocalDate.now()
                        startDate = endDate?.minusMonths(1)
                        loadDataForAdmin(selectedAuditorium,startDate,endDate)
                    }
                    "Custom range" -> {
                        binding.buttonLinearLayoutUtilizationStatistics.visibility = View.VISIBLE
                        val today = Calendar.getInstance()

                        val startDateButton = binding.startDateButtonUtilizationStatistics
                        val endDateButton = binding.endDateButtonUtilizationStatistics
                        val generateDataButton = binding.generateDataButtonUtilizationStatistics

                        startDateButton.setOnClickListener{
                            val datePickerStartDate = DatePickerDialog(this@AdminAnalyticsReportsUtilizationStatistics,
                                {_,year,month,day->
                                    startDate = LocalDate.of(year, month+1,day)
                                    startDateButton.text = String.format("%d-%02d-%02d", year, month+1,day)
                                },today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(
                                    Calendar.DAY_OF_MONTH))
                            datePickerStartDate.show()
                        }

                        endDateButton.setOnClickListener {
                            val datePickerEndDate = DatePickerDialog(this@AdminAnalyticsReportsUtilizationStatistics,
                                {_,year2,month2,day2 ->
                                    endDate = LocalDate.of(year2,month2+1,day2)
                                    endDateButton.text = String.format("%d-%02d-%02d", year2,month2+1,day2)
                                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(
                                    Calendar.DAY_OF_MONTH))

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
                                loadDataForAdmin(selectedAuditorium,startDateButtonDate, endDateButtonDate)
                            } else {
                                Toast.makeText(this@AdminAnalyticsReportsUtilizationStatistics, "Please select both dates", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }

    private fun initializeSpinnerAuditorium(){
        spinnerData.clear()

        val eventRef = FirebaseDatabase.getInstance().getReference("events")
        eventRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                for (data in snapshot.children){
                    val event = data.getValue(EventModelData::class.java)
                    if (event!=null){
                        spinnerData.add(event.eventLocation.toString())
                    }
                }
                val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,spinnerData.distinct())
                binding.spinnerAuditorium.adapter = adapter

                binding.spinnerAuditorium.setSelection(0)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error on database", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadDataForAdmin(auditorium: String?, startDate: LocalDate?, endDate: LocalDate?){
        if (auditorium == null || startDate == null || endDate == null) return

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val eventRef = FirebaseDatabase.getInstance().getReference("events")

        val auditoriumUsedDate = ArrayList<LocalDate>()

        eventRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val event = data.getValue(EventModelData::class.java) ?: continue

                        if (event.eventLocation == auditorium){
                            val eventDateStr = event.eventDate ?: continue

                            try {
                                val eventDate = LocalDate.parse(eventDateStr,formatter)

                                if (!eventDate.isBefore(startDate) && !eventDate.isAfter(endDate)){
                                    auditoriumUsedDate.add(eventDate)
                                }
                            }catch (e: Exception){
                                Toast.makeText(this, "error $e", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    val totalDays = ChronoUnit.DAYS.between(startDate,endDate)
                    utilizationAuditorium = (auditoriumUsedDate.size.toDouble() / totalDays) * 100
                    Log.d("TAG", "total days: $totalDays")
                    Log.d("TAG", "used days: ${auditoriumUsedDate.size}")
                    Log.d("TAG", "utilization: ${utilizationAuditorium.toInt()}%")
                    showPieChart(auditoriumUsedDate.size,totalDays.toInt())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPieChart(usedDays: Int, totalDays:Int) {
        val unUsedDays = totalDays - usedDays
        val entries = ArrayList<PieEntry>()

        val resultLayout = binding.resultLinearLayoutUtilizationStatistics
        val xText = binding.xTextViewUtilizationStatistics
        val yText = binding.yTextViewUtilizationStatistics
        val zText = binding.zTextViewUtilizationStatistics
        val vText = binding.vTextViewUtilizationStatistics

        entries.add(PieEntry(usedDays.toFloat()))
        entries.add(PieEntry(unUsedDays.toFloat()))
        if (usedDays == 0 && unUsedDays == 0){
            Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show()
        }

        val dataSet = PieDataSet(entries,"Utilization").apply {
            colors = listOf(
                Color.rgb(76,175,80),
                Color.rgb(244,67,54)
            )
        }

        val pieData = PieData(dataSet)

        val pieChart = binding.analyticReportPieChartUtilizationStatisticsd


        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Auditorium utilization"
        pieChart.invalidate()


        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    resultLayout.visibility = View.VISIBLE

                    xText.text = "Total day: $totalDays"
                    yText.text = "Used day: $usedDays"
                    zText.text = "Unused day: $unUsedDays"
                    vText.text = "Utilization auditorium: ${utilizationAuditorium.toInt()}%"
                }
            }

            override fun onNothingSelected() {
                resultLayout.visibility = View.GONE
            }

        })

    }
}