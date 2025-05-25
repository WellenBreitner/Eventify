package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.eventify.R

class AdminAnalyticsReportFragment : Fragment() {
    private lateinit var view: View
    private lateinit var event: Button
    private lateinit var auditorium: Button
    private lateinit var utilization: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_admin_analytics_report, container, false)

        initializeUI()
        initializeListener()
        return view
    }

    private fun initializeListener() {
        event.setOnClickListener{
            val intent = Intent(requireActivity(),AdminAnalyticsReportsEventHosted::class.java)
            startActivity(intent)
        }

        auditorium.setOnClickListener{
            val intent = Intent(requireActivity(),AdminAnalyticReportsAuditoriumBooking::class.java)
            startActivity(intent)
        }

        utilization.setOnClickListener{
            val intent = Intent(requireActivity(), AdminAnalyticsReportsUtilizationStatistics::class.java)
            startActivity(intent)
        }
    }

    private fun initializeUI() {
        event = view.findViewById(R.id.eventHostedButton)
        auditorium = view.findViewById(R.id.auditoriumBookingButton)
        utilization = view.findViewById(R.id.utilizationStatisticButton)

    }


}