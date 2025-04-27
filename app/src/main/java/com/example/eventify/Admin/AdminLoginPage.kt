package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.R
import com.example.eventify.attendees.AttendeesDashboard
import com.example.eventify.databinding.ActivityAdminLoginPageBinding
import com.example.eventify.eventOrganizer.EventOrganizerDashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminLoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.adminLoginButton.setOnClickListener {
            loginUser()
        }

        binding.adminMoveToSignUpButton.setOnClickListener {
            startActivity(Intent(this, AdminSignup::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val email = binding.loginEmail.text.toString().trim()
        val password = binding.loginPassword.text.toString()

        if (email.isEmpty()) {
            binding.loginEmail.error = "Email cannot be empty"
            binding.loginEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.loginPassword.error = "Password cannot be empty"
            binding.loginPassword.requestFocus()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userID = firebaseAuth.currentUser?.uid
                    if (userID != null) {
                        checkUserRole(userID)
                    }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRole(userID: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(userID)
        userRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val role = snapshot.child("role").getValue(String::class.java)
                    when (role) {
                        "admin" -> {
                            startActivity(Intent(this, AdminDashboard::class.java))
                            finish()
                        }
                        "attendees" -> {
                            startActivity(Intent(this, AttendeesDashboard::class.java))
                            finish()
                        }
                        else -> {
                            Toast.makeText(this, "Unknown role in users: $role", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    checkEventOrganizerRole(userID)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkEventOrganizerRole(userID: String) {
        val database = FirebaseDatabase.getInstance()
        val eoRef = database.getReference("event_organizers").child(userID)

        eoRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val role = snapshot.child("role").getValue(String::class.java)
                    if (role == "event organizer") {
                        val intent = Intent(this,EventOrganizerDashboard::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Unknown role in eventOrganizers: $role", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Account not found in system", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch Event Organizer data", Toast.LENGTH_SHORT).show()
            }
    }
}
