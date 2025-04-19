package com.example.eventify.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventify.ModelData.UserModelData
import com.example.eventify.databinding.ActivityRegisterEoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.*

class RegisterActivityEO : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterEoBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterEoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        usersRef     = FirebaseDatabase.getInstance().getReference("users")

        binding.signupButtonEO.setOnClickListener {
            val email = binding.signupEmailEO.text.toString().trim()
            val fullName = binding.signupFullName.text.toString().trim()
            val phone = binding.signupPhone.text.toString().trim()
            val organization = binding.signupOrganization.text.toString().trim()
            val password = "neweo123"
            val role = "Event Organizer"

            if (email.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Full name, email & phone are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { emailTask ->
                    if (!emailTask.isSuccessful) {
                        Toast.makeText(this, "Error checking email: ${emailTask.exception?.message}", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }

                    val methods = emailTask.result?.signInMethods
                    if (!methods.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "This email is already registered. Please log in or use a different email.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        usersRef.orderByChild("fullName")
                            .equalTo(fullName)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        Toast.makeText(
                                            this@RegisterActivityEO,
                                            "This full name is already in use. Please verify your name.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        createAuthUser(fullName, email, phone, organization, password, role)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        this@RegisterActivityEO,
                                        "Database error: ${error.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                    }
                }
        }
    }

    private fun createAuthUser(
        fullName: String,
        email: String,
        phone: String,
        organization: String,
        password: String,
        role: String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid
                    if (uid == null) {
                        Toast.makeText(this, "Failed to retrieve user ID", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    val organizer = UserModelData(uid, fullName, email, phone, organization, role)
                    usersRef.child(uid)
                        .setValue(organizer)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                startActivity(Intent(this, AdminDashboard::class.java))
                                //still error
                                firebaseAuth.sendPasswordResetEmail(email.trim()).addOnCompleteListener{ resetPassword ->
                                    if (resetPassword.isSuccessful){
                                        Intent(this,AdminDashboard::class.java).also {
                                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(it)
                                        }
                                    }else{
                                        Toast.makeText(this, "${resetPassword.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to save profile: ${dbTask.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }


                        }
                } else {
                    val ex = authTask.exception
                    if (ex is FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            this,
                            "This email is already registered. Please log in or use a different email.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Signup failed: ${ex?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
}
