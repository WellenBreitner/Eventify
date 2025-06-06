package com.example.eventify.attendeesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R

class AttendeesEventAdapter (
    private val eventTicketPairs: List<Pair<EventModelData, List<TicketModelData>>>
) : RecyclerView.Adapter<AttendeesEventAdapter.ViewHolder>() {
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
        val (event, tickets) = eventTicketPairs[position]

        val totalRemaining = tickets.sumOf { it.ticketRemaining ?: 0 }

        holder.eventName.text = event.eventName
        holder.eventLocation.text = "Location: ${event.eventLocation}"
        holder.eventDate.text = "Date: ${event.eventDate}  ${event.eventTime}"

        Glide.with(holder.itemView.context)
            .load(event.eventImage)
            .placeholder(R.drawable.event_default_image)
            .into(holder.eventImage)

        if (totalRemaining > 0) {
            holder.ticketAvailable.text = "Ticket Available: Available"
        } else {
            holder.ticketAvailable.text = "Ticket Available: Waiting List"
        }

        holder.itemView.setOnClickListener {
            val defaultTicket = tickets.firstOrNull()
            if (defaultTicket != null) {
                listener.onClickItem(event, defaultTicket, totalRemaining)
            }
        }
    }


    override fun getItemCount(): Int {
        return eventTicketPairs.size
    }

    interface onClickEventListener{
        fun onClickItem(dataEvent: EventModelData, dataTicket: TicketModelData, totalOfTicket: Int)
    }

}