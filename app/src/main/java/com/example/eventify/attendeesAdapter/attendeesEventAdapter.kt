package com.example.eventify.attendeesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.eventModelData
import com.example.eventify.R


class attendeesEventAdapter (
    private val eventList: List<eventModelData>
): RecyclerView.Adapter<attendeesEventAdapter.ViewHolder>() {

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val eventImage: ImageView = itemView.findViewById(R.id.attendeesEventImage)
        val eventName: TextView = itemView.findViewById(R.id.attendeesEventName)
        val eventDesc: TextView = itemView.findViewById(R.id.attendeesEventDescription)
        val eventDate: TextView = itemView.findViewById(R.id.attendeesEventDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): attendeesEventAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attendees_event_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: attendeesEventAdapter.ViewHolder, position: Int) {
        val event = eventList[position]
        holder.eventName.text = event.eventName
        holder.eventDesc.text = event.eventDescription
        holder.eventDate.text = event.eventDate
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}