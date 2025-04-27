package com.example.eventify.attendees

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAttendeesDashboardBinding
import com.example.eventify.databinding.ActivityAttendeesEventBookedDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AttendeesDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityAttendeesDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAttendeesDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        replaceFragment(AttendeesEventViewPager())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Home -> replaceFragment(AttendeesEventViewPager())
                R.id.Person -> replaceFragment(AttendeesAccount())
                else->{}
            }
            true
        }
    }

    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentFrameLayout,fragment)
        fragmentTransaction.commit()
    }
}