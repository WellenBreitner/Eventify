package com.example.eventify.attendeesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R


class AttendeesEventAdapter (
    private val eventList: List<EventModelData>
): RecyclerView.Adapter<AttendeesEventAdapter.ViewHolder>() {

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
            // use later
            //        val eventImage: ImageView = itemView.findViewById(R.id.attendeesEventImage)

        val eventName: TextView = itemView.findViewById(R.id.attendeesEventName)
        val eventDesc: TextView = itemView.findViewById(R.id.attendeesEventDescription)
        val eventDate: TextView = itemView.findViewById(R.id.attendeesEventDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):  ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attendees_event_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventList[position]
        holder.eventName.text = event.eventName
        holder.eventDesc.text = event.eventDescription
        holder.eventDate.text = event.eventDate
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}