package com.example.eventify.Admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.R
import com.example.eventify.databinding.ActivityEventOrganizerResetPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class EventOrganizerResetPassword : AppCompatActivity() {
    private lateinit var binding : ActivityEventOrganizerResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEventOrganizerResetPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user = FirebaseAuth.getInstance().currentUser

        binding.adminChangePasswordButton.setOnClickListener {
            if (binding.changePasswordTextView.text.toString().length < 8 || binding.changePasswordTextView.text.toString().isEmpty()){
                binding.changePasswordTextView.error = "Password must be at least 8 characters"
                binding.changePasswordTextView.requestFocus()
            }else{
                user?.updatePassword(binding.changePasswordTextView.text.toString())
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                        Toast.makeText(this, "Failed to update password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                    }
            }
        }

    }
}