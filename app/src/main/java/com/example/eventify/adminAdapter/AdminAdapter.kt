package com.example.eventify.adminAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventOrganizerModelData
import com.example.eventify.R

class AdminAdapter (private val EOList: List<EventOrganizerModelData>): RecyclerView.Adapter<AdminAdapter.ViewHolder>() {

    private lateinit var listener : eventOrganizerOnCLick

    fun setEventOrganizerOnClick(listener:eventOrganizerOnCLick){
        this.listener = listener
    }

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val eventOrganizerName = itemView.findViewById<TextView>(R.id.EventOrganizerNameTextView)
        val eventOrganizerEmail = itemView.findViewById<TextView>(R.id.EventOrganizerEmailTextView)
        val eventOrganizerPhone = itemView.findViewById<TextView>(R.id.EventOrganizerPhoneNumberTextView)
        val eventOrganizerOrgName = itemView.findViewById<TextView>(R.id.EventOrganizerOrganizationNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.event_organizer_dahsboard_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventOrganizer = EOList[position]
        holder.eventOrganizerName.text = "Name: ${eventOrganizer.fullName}"
        holder.eventOrganizerPhone.text = "Phone Number: ${eventOrganizer.phone}"
        holder.eventOrganizerEmail.text = "Email: ${eventOrganizer.email}"
        holder.eventOrganizerOrgName.text = "Organization Name: ${eventOrganizer.organization}"

        holder.itemView.setOnClickListener {
            listener.onClick(eventOrganizer)
        }
    }

    override fun getItemCount(): Int {
        return EOList.size
    }

    interface eventOrganizerOnCLick{
        fun onClick(data: EventOrganizerModelData)
    }
}