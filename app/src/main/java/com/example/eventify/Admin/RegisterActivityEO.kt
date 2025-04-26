package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.EventOrganizerModelData
import com.example.eventify.databinding.ActivityRegisterEoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivityEO : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterEoBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterEoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButtonEO.setOnClickListener { registerEventOrganizer() }
    }

    private fun registerEventOrganizer() {
        val fullName = binding.signupFullName.text.toString().trim()
        val email = binding.signupEmailEO.text.toString().trim()
        val phone = binding.signupPhone.text.toString().trim()
        val organization = binding.signupOrganization.text.toString().trim()
        val password = "neweo123"

        if (!validateInput(fullName, email, phone)) return

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userID = firebaseAuth.currentUser?.uid
                    if (userID != null) {
                        saveEventOrganizerData(userID, fullName, email, phone, organization)
                    } else {
                        showToast("User ID not found!")
                    }
                } else {
                    showToast(task.exception?.localizedMessage ?: "Registration failed")
                }
            }
    }

    private fun validateInput(fullName: String, email: String, phone: String): Boolean {
        var isValid = true

        if (fullName.isEmpty()) {
            binding.signupFullName.error = "Full name cannot be empty"
            if (isValid) binding.signupFullName.requestFocus()
            isValid = false
        }

        if (email.isEmpty()) {
            binding.signupEmailEO.error = "Email cannot be empty"
            if (isValid) binding.signupEmailEO.requestFocus()
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signupEmailEO.error = "Please enter a valid email"
            if (isValid) binding.signupEmailEO.requestFocus()
            isValid = false
        }

        if (phone.isEmpty()) {
            binding.signupPhone.error = "Phone number cannot be empty"
            if (isValid) binding.signupPhone.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun saveEventOrganizerData(userID: String, fullName: String, email: String, phone: String, organization: String) {
        val database = FirebaseDatabase.getInstance()
        val organizersRef = database.getReference("event_organizers")

        val organizerData = EventOrganizerModelData(
            eventOrganizerID = userID,
            fullName = fullName,
            email = email,
            phone = phone,
            organization = organization,
        )

        organizersRef.child(userID).setValue(organizerData)
            .addOnSuccessListener {
                showToast("Event Organizer account created successfully")
                sendPasswordResetEmail(email)
                startActivity(Intent(this, AdminDashboard::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                showToast("Failed to save Event Organizer data: ${e.localizedMessage}")
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Password reset email sent")
                } else {
                    showToast("Failed to send password reset email: ${task.exception?.localizedMessage}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
