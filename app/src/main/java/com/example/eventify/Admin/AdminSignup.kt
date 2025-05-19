package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.UserModelData
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAdminSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminSignup : AppCompatActivity() {

    private lateinit var binding: ActivityAdminSignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.adminSignUpButton.setOnClickListener {
            registerAdmin()
        }

        binding.adminMoveToLoginButton.setOnClickListener {
            startActivity(Intent(this, AdminLoginPage::class.java))
            finish()
        }
    }

    private fun registerAdmin() {
        val username = binding.signupUsername.text.toString().trim()
        val email = binding.signupEmail.text.toString().trim()
        val password = binding.signupPassword.text.toString()
        val confirmPassword = binding.signupConfirm.text.toString()

        var isValid = true

        if (username.isEmpty()) {
            binding.signupUsername.error = "Username cannot be empty"
            if (isValid) binding.signupUsername.requestFocus()
            isValid = false
        }

        if (email.isEmpty()) {
            binding.signupEmail.error = "Email cannot be empty"
            if (isValid) binding.signupEmail.requestFocus()
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signupEmail.error = "Please enter a valid email"
            if (isValid) binding.signupEmail.requestFocus()
            isValid = false
        }

        if (password.isEmpty()) {
            binding.signupPassword.error = "Password cannot be empty"
            if (isValid) binding.signupPassword.requestFocus()
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.signupConfirm.error = "Confirm Password cannot be empty"
            if (isValid) binding.signupConfirm.requestFocus()
            isValid = false
        }


        if (!isValid) return

        if (password.length < 8) {
            binding.signupPassword.error = "Password must be at least 8 characters"
            binding.signupPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            binding.signupConfirm.error = "Passwords do not match"
            binding.signupConfirm.requestFocus()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userID = firebaseAuth.currentUser?.uid
                    if (userID != null) {
                        saveUserData(userID, username, email)
                    } else {
                        Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(userID: String, username: String, email: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val userData = UserModelData(
            username = username,
            userID = userID,
            email = email,
            role = "attendees"
        )

        usersRef.child(userID).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AdminLoginPage::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}
