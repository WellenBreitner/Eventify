package com.example.eventify.eventOrganizer

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.eventify.Admin.EventOrganizerResetPassword
import com.example.eventify.R
import com.example.eventify.databinding.ActivityEventOrganizerDashboardBinding
import com.google.android.material.snackbar.Snackbar

class EventOrganizerDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityEventOrganizerDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventOrganizerDashboardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        replaceFragment(EventOrganizerHome())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.home -> replaceFragment(EventOrganizerHome())
                R.id.analyticReport -> replaceFragment(EOAnalyticsReport())
                R.id.profile -> replaceFragment(EventOrganizerProfile())
                else -> {}
            }
            true
        }

        val showSnackbar = intent.getBooleanExtra("showSnackbar", false)

        if (showSnackbar) {
            Snackbar.make(findViewById(android.R.id.content), "Please change your password!", Snackbar.LENGTH_LONG)
                .setAction("Change Now") {
                    val intent = Intent(this,EventOrganizerResetPassword::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}