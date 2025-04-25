package com.example.eventify.attendeesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.BookingModelData
import com.example.eventify.R

class AttendeesEventBookedAdapter (
    private val eventBookedList:List<BookingModelData>
) : RecyclerView.Adapter<AttendeesEventBookedAdapter.ViewHolder>()
{

    private lateinit var listener: eventBookedClick

    fun setOnEventBookedClick(listener: eventBookedClick){
        this.listener = listener
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val eventName: TextView = itemView.findViewById(R.id.attendeesEventBookedName)
        val eventDate: TextView = itemView.findViewById(R.id.attendeesEventBookedDate)
        val eventLocation: TextView = itemView.findViewById(R.id.attendeesEventBookedLocation)
        val seat: TextView = itemView.findViewById(R.id.attendeesEventBookedSeat)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attendees_event_booked_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookedEvent = eventBookedList[position]
        holder.eventName.text = bookedEvent.eventName
        holder.eventDate.text = "Date: ${bookedEvent.eventDate}"
        holder.eventLocation.text = "Location: ${bookedEvent.eventLocation}"
        holder.seat.text = "Seat: ${bookedEvent.selectedSeat.joinToString(",")}"

        holder.itemView.setOnClickListener{
            listener.onClick(bookedEvent)
        }
    }

    override fun getItemCount(): Int {
        return eventBookedList.size
    }

    interface eventBookedClick{
        fun onClick(dataBooking: BookingModelData)
    }
}