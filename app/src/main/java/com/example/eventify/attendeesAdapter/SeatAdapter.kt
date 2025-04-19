package com.example.eventify.attendeesAdapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.SeatModelData
import com.example.eventify.R
import com.google.android.material.button.MaterialButton

class SeatAdapter(private val seatList: MutableList<SeatModelData>,
    private val unavailableSeats: HashSet<String>) :
    RecyclerView.Adapter<SeatAdapter.ViewHolder>(){
    private lateinit var listener: seatOnClick

    fun setSeatOnClick(listener: seatOnClick){
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val seatButton: MaterialButton =  itemView.findViewById(R.id.seat_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.seat_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seat = seatList[position]
        holder.seatButton.text = seat.label

        holder.seatButton.isEnabled = true
        holder.seatButton.setTextColor(Color.WHITE)

        val backgroundColor = if (seat.isSelected) "#A62C2C" else "#5F8B4C"
        holder.seatButton.setBackgroundColor(Color.parseColor(backgroundColor))

        if (seat.label in unavailableSeats) {
            holder.seatButton.setBackgroundColor(Color.parseColor("#F5F5F5"))
            holder.seatButton.isEnabled = false
            holder.seatButton.setTextColor(Color.BLACK)
        }

        holder.seatButton.setOnClickListener {
            listener.onClick(seat)
            val newColor = if (seat.isSelected) "#A62C2C" else "#5F8B4C"
            holder.seatButton.setBackgroundColor(Color.parseColor(newColor))
        }
    }


    override fun getItemCount(): Int {
        return seatList.size
    }

    interface seatOnClick{
        fun onClick(seatModelData: SeatModelData)
    }



}