package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.R
import com.example.eventify.databinding.ActivityAdminLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class AdminLoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()

        binding.adminLoginButton.setOnClickListener{
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email, password) .addOnCompleteListener{
                    if (it.isSuccessful){
                        val intent = Intent(this, AdminDashboard::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.adminMoveToSignUpButton.setOnClickListener{
            val signupIntent = Intent(this, AdminSignup::class.java)
            startActivity(signupIntent)
            finish()
        }
    }
}