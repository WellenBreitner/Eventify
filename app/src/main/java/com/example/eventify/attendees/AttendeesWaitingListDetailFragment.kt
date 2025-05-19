package com.example.eventify.attendees

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.WaitingListModelData
import com.example.eventify.R
import com.google.firebase.database.FirebaseDatabase

class AttendeesWaitingListDetailFragment : DialogFragment() {
    private lateinit var view: View
    private lateinit var waitingListData: WaitingListModelData
    private lateinit var waitingListID: TextView
    private lateinit var waitingListEventID: TextView
    private lateinit var waitingListEmail: TextView
    private lateinit var waitingListEventName: TextView
    private lateinit var waitingListEventDate: TextView
    private lateinit var waitingListEventLocation: TextView
    private lateinit var waitingListCloseButton: Button
    private lateinit var leaveWaitListButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_attendees_waiting_list_detail, container, false)

        initializeUI()
        initializeListener()

        return view
    }

    private fun initializeUI() {
        waitingListID = view.findViewById(R.id.AttendeesWaitingListDetailID)
        waitingListEventID = view.findViewById(R.id.AttendeesWaitingListDetailEventID)
        waitingListEmail = view.findViewById(R.id.AttendeesWaitingListDetailEmail)
        waitingListEventName = view.findViewById(R.id.AttendeesWaitingListDetailEventName)
        waitingListEventDate = view.findViewById(R.id.AttendeesWaitingListDetailEventDate)
        waitingListEventLocation = view.findViewById(R.id.AttendeesWaitingListDetailEventLocation)
        leaveWaitListButton = view.findViewById(R.id.AttendeesLeaveWaitingListButton)
        waitingListCloseButton = view.findViewById(R.id.AttendeesWaitingListCloseButton)

        val argument = if (Build.VERSION.SDK_INT >=  33){
            arguments?.getParcelable("waiting_list_data",WaitingListModelData::class.java)
        }else{
            @Suppress("DEPRECATION")
            arguments?.getParcelable("waiting_list_data")
        }

        if (argument!=null){
            waitingListData = argument
            waitingListID.text = "Waiting List ID: ${waitingListData.waitingListId}"
            waitingListEventID.text = "Waiting list event ID: ${waitingListData.eventId}"
            waitingListEmail.text = "Waiting list email: ${waitingListData.email}"
        }else {
            Toast.makeText(requireContext(), "No data received", Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
            return
        }

        val eventDatabaseReference = FirebaseDatabase.getInstance().getReference("events")
        eventDatabaseReference.child(waitingListData.eventId.toString()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    val eventData = snapshot.getValue(EventModelData::class.java)
                        if (eventData!=null){
                            waitingListEventName.text = "Waiting list event name: ${eventData.eventName}"
                            waitingListEventDate.text = "Waiting list event date: ${eventData.eventDate}"
                            waitingListEventLocation.text = "Waiting list event location: ${eventData.eventLocation}"
                        }
                }
            }
    }

    private fun initializeListener() {
        waitingListCloseButton.setOnClickListener {
            dialog?.dismiss()
        }

        leaveWaitListButton.setOnClickListener {
            leaveWaitingListOnClick()
        }
    }

    private fun leaveWaitingListOnClick() {
        val waitingListRef = FirebaseDatabase.getInstance().getReference("waiting_lists")
        waitingListData.waitingListId?.let { id ->
            waitingListRef.child(id).removeValue()
                .addOnSuccessListener {
                    context?.let {
                        Toast.makeText(it, "Left the waiting list successfully", Toast.LENGTH_SHORT).show()
                    }
                    dismiss()
                }
                .addOnFailureListener {
                    context?.let {
                        Toast.makeText(it, "Failed to leave the waiting list", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}