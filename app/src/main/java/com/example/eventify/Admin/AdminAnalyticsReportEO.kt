package com.example.eventify.Admin

import android.content.ContentValues
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAdminAnalyticsReportEoBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdminAnalyticsReportEO : AppCompatActivity() {
    private lateinit var eo_id: String
    private lateinit var binding: ActivityAdminAnalyticsReportEoBinding
    private lateinit var selectedTypeReport: String
    private lateinit var selectedTimePeriod: String

    companion object{
        const val EVENT_ORGANIZER_DATA = "EVENT_ORGANIZER_DATA"
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminAnalyticsReportEoBinding.inflate(layoutInflater)
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
        val getEventOrganizerID = intent.getStringExtra(EVENT_ORGANIZER_DATA)
        eo_id = getEventOrganizerID.toString()

        val spinnerTypeReport = binding.spinnerTypeReport
        spinnerTypeReport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTypeReport = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        val spinnerTimePeriod = binding.spinnerTimePeriodEo
        spinnerTimePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTimePeriod = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initializeListener() {
        binding.generateDataButton.setOnClickListener{
            getSpinnerData(eo_id,selectedTypeReport,selectedTimePeriod)
        }

        binding.PDFButton.setOnClickListener{
            downloadAsPDF(binding.analyticReportBarChartEO, "Analytics_Report_EO_pdf")
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

    private fun getSpinnerData(eventOrganizerID: String, spinnerTypeReport: String, spinnerTimePeriod: String ) {
        var startDate : LocalDate? = null
        var endDate : LocalDate? = null

        when(selectedTimePeriod){
            "Daily" -> {
                endDate = LocalDate.now()
                startDate = endDate?.minusDays(1)
                loadDataForUser(eventOrganizerID,startDate,endDate,spinnerTypeReport)
            }
            "Weekly" -> {
                endDate = LocalDate.now()
                startDate = endDate?.minusWeeks(1)
                loadDataForUser(eventOrganizerID,startDate,endDate,spinnerTypeReport)
            }
            "Monthly" -> {
                endDate = LocalDate.now()
                startDate = endDate?.minusMonths(1)
                loadDataForUser(eventOrganizerID,startDate,endDate,spinnerTypeReport)
            }
        }
    }

    private fun loadDataForUser(eoID: String, startDate: LocalDate?, endDate: LocalDate? ,spinnerTypeReport: String){
        if (startDate == null || endDate == null) return

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val booking = FirebaseDatabase.getInstance().getReference("bookings")

        val revenue = mutableMapOf<String,Int>()
        val sales = mutableMapOf<String,Int>()
        val seatOccupancy = mutableMapOf<String,Int>()

        booking.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val booking = data.getValue(BookingModelData::class.java)
                        if (booking != null && booking.eventOrganizerID == eoID){
                            val dateBooked = booking.bookedAt
                            val totalPrice = booking.totalPrice
                            val totalSales = booking.numberOfTicket
                            val selectedSeats = booking.selectedSeat

                            when(spinnerTypeReport){
                                "Revenue" -> {
                                    try {
                                        val bookingDate = LocalDate.parse(dateBooked,formatter)
                                        val bookingID = booking.bookingId.toString()
                                        if (bookingDate in startDate..endDate){
                                            if (totalPrice != null) {
                                                revenue[bookingID] = revenue.getOrDefault(bookingID,0) + totalPrice.toInt()
                                                showRevenueBarChart(revenue)
                                            }
                                        }
                                    }catch (e: Exception){
                                        Toast.makeText(this, "error $e", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "Ticket sales" -> {
                                    try{
                                        val bookingDate = LocalDate.parse(dateBooked,formatter)
                                        val bookingID = booking.bookingId.toString()
                                        if (bookingDate in startDate..endDate){
                                            if (totalSales != null) {
                                                sales[bookingID] = sales.getOrDefault(bookingID,0) + totalSales.toInt()
                                                showSalesBarChart(sales)
                                            }
                                        }
                                    }catch (e: Exception){
                                        Toast.makeText(this, "error $e", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "Seat occupancy" -> {
                                    val bookingDate = LocalDate.parse(dateBooked,formatter)
                                    val bookingData = booking.bookingId.toString()
                                    if (bookingDate in startDate..endDate) {
                                        selectedSeats.forEach { seat ->
                                            seatOccupancy[bookingData] = seatOccupancy.getOrDefault(bookingData, 0) + 1
                                        }
                                        showSeatOccupancyBarChart(seatOccupancy)
                                    }
                                }
                            }
                        }
                    }
                }
            }.addOnFailureListener { snapshot ->
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showSeatOccupancyBarChart(data: MutableMap<String, Int>) {
        val resultLayout = binding.resultLinearLayoutEO
        val xText = binding.xTextViewEO
        val yText = binding.yTextViewEO

        val barChart = binding.analyticReportBarChartEO
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        if (data.isEmpty()) {
            Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show()
            barChart.description.text = "Data is empty"
            return
        }

        val bookingRef = FirebaseDatabase.getInstance().getReference("bookings")

        bookingRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val bookingIdToEventName = mutableMapOf<String, String>()

                for (dataSnap in snapshot.children) {
                    val booking = dataSnap.getValue(BookingModelData::class.java)
                    if (booking != null) {
                        val id = booking.bookingId ?: dataSnap.key
                        val name = booking.eventName ?: "Unknown Event"

                        if (id != null) {
                            bookingIdToEventName[id] = name
                        }
                    }
                }

                val groupedByEvent = mutableMapOf<String, Int>()
                for ((bookingID, count) in data) {
                    val eventName = bookingIdToEventName[bookingID] ?: "Unknown Event"
                    groupedByEvent[eventName] = groupedByEvent.getOrDefault(eventName, 0) + count
                }

                entries.clear()
                labels.clear()
                var index = 0f
                for ((eventName, totalCount) in groupedByEvent) {
                    entries.add(BarEntry(index, totalCount.toFloat()))
                    labels.add(eventName)
                    index += 1f
                }


                val dataSet = BarDataSet(entries, "Seat Occupancy per Event")
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())
                val barData = BarData(dataSet)
                barData.barWidth = 0.9f

                barChart.data = barData
                barChart.description.text = "Seat Occupancy per Event"
                barChart.setFitBars(true)

                barChart.invalidate()

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show()
        }

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val clickedEvent = labels[e.x.toInt()]
                    val totalCount = e.y.toInt()

                    resultLayout.visibility = View.VISIBLE
                    xText.text = "Event: $clickedEvent"
                    yText.text = "Total Occupied Seats: $totalCount"
                }
            }

            override fun onNothingSelected() {
                resultLayout.visibility = View.GONE
            }
        })

    }

    private fun showRevenueBarChart(data: MutableMap<String, Int>) {
        val resultLayout = binding.resultLinearLayoutEO
        val xText = binding.xTextViewEO
        val yText = binding.yTextViewEO

        val barChart = binding.analyticReportBarChartEO
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        if (data.isEmpty()) {
            Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show()
            barChart.description.text = "Data is empty"
            return
        }

        val bookingRef = FirebaseDatabase.getInstance().getReference("bookings")

        bookingRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val bookingIdToEventName = mutableMapOf<String, String>()

                for (dataSnap in snapshot.children) {
                    val booking = dataSnap.getValue(BookingModelData::class.java)
                    if (booking != null) {
                        val id = booking.bookingId ?: dataSnap.key
                        val name = booking.eventName ?: "Unknown Event"
                        if (id != null) {
                            bookingIdToEventName[id] = name
                        }
                    }
                }

                val groupedByEvent = mutableMapOf<String, Float>()
                for ((bookingID, count) in data) {
                    val eventName = bookingIdToEventName[bookingID] ?: "Unknown Event"
                    groupedByEvent[eventName] = groupedByEvent.getOrDefault(eventName, 0f) + count.toFloat()
                }

                entries.clear()
                labels.clear()
                var index = 0f
                for ((eventName, totalCount) in groupedByEvent) {
                    entries.add(BarEntry(index, totalCount))
                    labels.add(eventName)
                    index += 1f
                }

                val dataSet = BarDataSet(entries, "Total Revenue per Event")
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())
                val barData = BarData(dataSet)
                barData.barWidth = 0.9f

                barChart.data = barData
                barChart.description.text = "Total revenue per Event"
                barChart.setFitBars(true)

                barChart.invalidate()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show()
        }

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val clickedEvent = labels[e.x.toInt()]
                    val totalCount = e.y.toInt()

                    resultLayout.visibility = View.VISIBLE
                    xText.text = "Event: $clickedEvent"
                    yText.text = "Total Bookings: $$totalCount"
                }
            }

            override fun onNothingSelected() {
                resultLayout.visibility = View.GONE
            }
        })
    }


    private fun showSalesBarChart(data: MutableMap<String, Int>) {
        val resultLayout = binding.resultLinearLayoutEO
        val xText = binding.xTextViewEO
        val yText = binding.yTextViewEO

        val barChart = binding.analyticReportBarChartEO
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        if (data.isEmpty()) {
            Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show()
            barChart.description.text = "Data is empty"
            return
        }

        val bookingRef = FirebaseDatabase.getInstance().getReference("bookings")

        bookingRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val bookingIdToEventName = mutableMapOf<String, String>()

                for (dataSnap in snapshot.children) {
                    val booking = dataSnap.getValue(BookingModelData::class.java)
                    if (booking != null) {
                        val id = booking.bookingId ?: dataSnap.key
                        val name = booking.eventName ?: "Unknown Event"
                        if (id != null) {
                            bookingIdToEventName[id] = name
                        }
                    }
                }

                val groupedByEvent = mutableMapOf<String, Float>()
                for ((bookingID, count) in data) {
                    val eventName = bookingIdToEventName[bookingID] ?: "Unknown Event"
                    groupedByEvent[eventName] = groupedByEvent.getOrDefault(eventName, 0f) + count.toFloat()
                }

                entries.clear()
                labels.clear()
                var index = 0f
                for ((eventName, totalCount) in groupedByEvent) {
                    entries.add(BarEntry(index, totalCount))
                    labels.add(eventName)
                    index += 1f
                }

                val dataSet = BarDataSet(entries, "Total sales per Event")
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())
                val barData = BarData(dataSet)
                barData.barWidth = 0.9f

                barChart.data = barData
                barChart.description.text = "Total sales per Event"
                barChart.setFitBars(true)

                barChart.invalidate()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show()
        }

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val clickedEvent = labels[e.x.toInt()]
                    val totalCount = e.y.toInt()

                    resultLayout.visibility = View.VISIBLE
                    xText.text = "Event: $clickedEvent"
                    yText.text = "Total sales: $totalCount ticket"
                }
            }

            override fun onNothingSelected() {
                resultLayout.visibility = View.GONE
            }
        })
    }
}