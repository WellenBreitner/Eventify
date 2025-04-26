package com.example.eventify.attendeesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R

class AttendeesEventAdapter (
    private val eventList: List<EventModelData>
): RecyclerView.Adapter<AttendeesEventAdapter.ViewHolder>() {
    private lateinit var listener: onClickEventListener

    fun setOnClickEventListener(listener: onClickEventListener){
        this.listener = listener
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val eventImage: ImageView = itemView.findViewById(R.id.attendeesEventListImage)
        val eventName: TextView = itemView.findViewById(R.id.attendeesEventListName)
        val eventLocation: TextView = itemView.findViewById(R.id.attendeesEventListLocation)
        val eventDate: TextView = itemView.findViewById(R.id.attendeesEventListDate)
        val ticketAvailable: TextView = itemView.findViewById(R.id.attendeesEventListTicketAvailable)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):  ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attendees_event_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventList[position]
        if (event.eventImage == null){
            holder.eventImage.setImageResource(R.color.black)
        }else{
            holder.eventImage.setImageResource(event.eventImage)
        }
        holder.eventName.text = event.eventName
        holder.eventLocation.text = "Location: ${event.eventLocation}"
        holder.eventDate.text = "Date: ${event.eventDate}"

        if (event.ticket?.ticketAvailable == true){
            holder.ticketAvailable.text = "Ticket Available: Available"
        }else{
            holder.ticketAvailable.text = "Ticket Available: Not Available"
        }

        holder.itemView.setOnClickListener {
            listener.onClickItem(event)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    interface onClickEventListener{
        fun onClickItem(dataEvent: EventModelData)
    }

}