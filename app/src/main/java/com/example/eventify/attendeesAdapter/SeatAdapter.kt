package com.example.eventify.attendeesAdapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.SeatModelData
import com.example.eventify.R
import com.example.eventify.attendeesViewModel.TicketTypeViewModel
import com.google.android.material.button.MaterialButton

class SeatAdapter(private val seatList: MutableList<SeatModelData>) :
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

        when(seat.isSelected){
            true -> holder.seatButton.setBackgroundColor(Color.parseColor("#A62C2C"))
            false-> holder.seatButton.setBackgroundColor(Color.parseColor("#5F8B4C"))
        }

        holder.seatButton.setOnClickListener {
            listener.onClick(seat)
            when(seat.isSelected){
                true -> holder.seatButton.setBackgroundColor(Color.parseColor("#A62C2C"))
                false-> holder.seatButton.setBackgroundColor(Color.parseColor("#5F8B4C"))
            }
        }
    }

    override fun getItemCount(): Int {
        return seatList.size
    }

    interface seatOnClick{
        fun onClick(seatModelData: SeatModelData)
    }



}