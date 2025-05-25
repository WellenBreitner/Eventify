package com.example.eventify.eventOrganizer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.eventify.Admin.AdminLoginPage
import com.example.eventify.ModelData.EventOrganizerModelData
import com.example.eventify.ModelData.UserModelData
import com.example.eventify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * A simple [Fragment] subclass.
 * Use the [EventOrganizerProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventOrganizerProfile : Fragment() {

    private lateinit var rootView: View
    private lateinit var emailTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_eo_profile, container, false)

        emailTextView = rootView.findViewById(R.id.emailTextView)
        usernameTextView = rootView.findViewById(R.id.usernameTextView)
        logoutButton = rootView.findViewById(R.id.logoutButton)
        firebaseAuth = FirebaseAuth.getInstance()

        initializeListener()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            val intent = Intent(requireActivity(), AdminLoginPage::class.java)
            startActivity(intent)
            requireActivity().finish()
            return
        }

        val userDatabaseReference = FirebaseDatabase.getInstance().getReference("event_organizers")
        userDatabaseReference.child(currentUser.uid).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(EventOrganizerModelData::class.java)
                    if (userData != null) {
                        Log.d("AttendeesAccount", "UserID: ${userData.eventOrganizerID}")
                        if (userData.eventOrganizerID == currentUser.uid) {
                            usernameTextView.text = userData.fullName
                            emailTextView.text = userData.email
                        }
                    }
                } else {
                    Log.w("AttendeesAccount", "User data not found in database")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AttendeesAccount", "Failed to get user data", e)
            }
    }

    private fun initializeListener() {
        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(requireActivity(), AdminLoginPage::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}