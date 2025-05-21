package com.example.eventify.attendees

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.eventify.Admin.AdminLoginPage
import com.example.eventify.ModelData.WaitingListModelData
import com.example.eventify.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.database

class AttendeesEventWaitingListEmail (private val eventID:String, private val maxWaitingList:Int): DialogFragment() {

    private lateinit var view:View;
    private lateinit var emailEditText: EditText
    private lateinit var addEmailButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(
            R.layout.fragment_attendees_event_waiting_list_email,
            container,
            false
        )

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser
        if (user == null){
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(),AdminLoginPage::class.java)
            startActivity(intent)
            requireActivity().finish()
        }else{
            user = firebaseAuth.currentUser
        }

        initializeUI()
        initializeListener()

        return view
    }

    private fun initializeListener() {
        addEmailButton.setOnClickListener {
            addEmailWaitingList()
        }

    }

    private fun initializeUI() {
        emailEditText = view.findViewById(R.id.emailWaitingList)
        addEmailButton = view.findViewById(R.id.addWaitingListEmailButton)
    }

    private fun addEmailWaitingList(){
        var emailAlreadyExists = false
        var isWaitingListFull = false
        var userWaitingListCount = 0
        val waitingListDatabaseReference = Firebase.database.getReference("waiting_lists")
        val waitingListEmail = emailEditText.text.toString().trim()
            if (emailEditText.text.isEmpty()){
                emailEditText.error = "Email cannot be empty"
                emailEditText.requestFocus()
            }else if (!Patterns.EMAIL_ADDRESS.matcher(waitingListEmail).matches()){
                emailEditText.error = "Email format not valid"
                emailEditText.requestFocus()
            }
            else {
                waitingListDatabaseReference.get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            for (data in snapshot.children) {
                                val waitingListData = data.getValue(WaitingListModelData::class.java)
                                if (waitingListData != null &&
                                    waitingListData.eventId == eventID
                                ) {
                                    if (waitingListData.email == waitingListEmail){
                                        emailAlreadyExists = true
                                        break
                                    }

                                    if (waitingListData.status == "active") {
                                        userWaitingListCount++
                                        if (userWaitingListCount >= maxWaitingList) {
                                            isWaitingListFull = true
                                            break
                                        }
                                    }
                                }
                            }

                            if (emailAlreadyExists) {
                                emailEditText.error = "The email is already in the waiting list for this event"
                                emailEditText.requestFocus()
                            }  else if (isWaitingListFull == true) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Waiting list for this event is full",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else {
                                val pushKey = waitingListDatabaseReference.push().key
                                val newData = WaitingListModelData(
                                    pushKey.toString(),
                                    eventID,
                                    user?.uid.toString(),
                                    waitingListEmail,
                                    "active",
                                    false,
                                    System.currentTimeMillis()
                                )
                                waitingListDatabaseReference.child(pushKey.toString()).setValue(newData)
                                userWaitingListCount++
                                Toast.makeText(requireActivity(), "You have been added into the waiting list", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        } else {
                            val pushKey = waitingListDatabaseReference.push().key
                            val newData = WaitingListModelData(
                                pushKey.toString(),
                                eventID,
                                user?.uid.toString(),
                                waitingListEmail,
                               "active",
                                false,
                                System.currentTimeMillis()
                            )
                            waitingListDatabaseReference.child(pushKey.toString()).setValue(newData)
                            userWaitingListCount++
                            Toast.makeText(requireActivity(), "You have been added into the waiting list", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
            }
    }


}