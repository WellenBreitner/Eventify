package com.example.eventify.Admin

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.ModelData.EventOrganizerModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAdminAnalyticsReportsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdminAnalyticsReports : AppCompatActivity() {

    private lateinit var eo_id: String
    private lateinit var binding: ActivityAdminAnalyticsReportsBinding
    private lateinit var barList: ArrayList<BarEntry>
    private lateinit var barDataSet: BarDataSet
    private lateinit var barData: BarData
    companion object{
        const val EVENT_ORGANIZER_DATA = "EVENT_ORGRANIZER_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminAnalyticsReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getEvent =
            if(Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(EVENT_ORGANIZER_DATA,EventOrganizerModelData::class.java)
            }else{
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EVENT_ORGANIZER_DATA)
            }

        eo_id = getEvent?.eventOrganizerID.toString()

        getAllBookings()

        val barChart = binding.analyticReportBarChart

        val daftarPenjualan = arrayOf(
            "Produk A" to 500f,
            "Produk B" to 100f,
            "Produk C" to 300f,
            "Produk D" to 800f,
            "Produk E" to 400f,
            "Produk F" to 1000f,
            "Produk G" to 800f
        )

        // Membuat array BarEntry dari data penjualan
        barList = ArrayList()
        for ((index, penjualan) in daftarPenjualan.withIndex()) {
            // Menambahkan data penjualan ke barList
            barList.add(BarEntry((index + 1).toFloat(), penjualan.second))
        }

        // Membuat BarDataSet
        barDataSet = BarDataSet(barList, "Penjualan Produk")
        barData = BarData(barDataSet)
        barChart.data = barData

        // Mengatur warna dan teks nilai
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 15f
        barDataSet.setDrawValues(true)  // Menampilkan nilai pada bar

        // Menambahkan nama produk pada X-Axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setGranularity(1f)  // Set langkah X-axis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Menentukan nama produk berdasarkan index
                return when (value) {
                    1f -> "Produk A"
                    2f -> "Produk B"
                    3f -> "Produk C"
                    4f -> "Produk D"
                    5f -> "Produk E"
                    6f -> "Produk F"
                    7f -> "Produk G"
                    else -> ""
                }
            }
        }

        // Menampilkan data pada BarChart
        barChart.invalidate()

    }

    private fun getAllBookings() {
        val user = FirebaseAuth.getInstance().currentUser
        val firebase = FirebaseDatabase.getInstance()
        val bookings = firebase.getReference("bookings")

        bookings.get()
            .addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                for (data in snapshot.children){
                    val booking = data.getValue(BookingModelData::class.java)
                    if (booking != null){
                        Log.d("TAG", "booking event name: ${booking.eventName}")
                    }
                }
            }
        }
    }
}