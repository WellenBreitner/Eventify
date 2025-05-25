package com.example.eventify.eoAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R

class EOTicketList(private val ticketTypeList: ArrayList<TicketModelData>) : RecyclerView.Adapter<EOTicketList.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ticketIDInfo: TextView = itemView.findViewById(R.id.ticketIDInfo)
        val ticketTypeInfo: TextView = itemView.findViewById(R.id.ticketTypeInfo)
        val ticketPriceInfo: TextView = itemView.findViewById(R.id.ticketPriceInfo)
        val ticketLimitInfo: TextView = itemView.findViewById(R.id.ticketLimitInfo)
        val ticketRemainingInfo: TextView = itemView.findViewById(R.id.ticketRemainingInfo)
        val ticketPromotionCodeInfo: TextView = itemView.findViewById(R.id.ticketPromotionCodeInfo)
        val ticketPromotionExpiryInfo: TextView = itemView.findViewById(R.id.ticketPromotionExpiryInfo)
        val ticketDiscountInfo: TextView = itemView.findViewById(R.id.ticketDiscountInfo)
        val ticketAssignSeatInfo: TextView = itemView.findViewById(R.id.ticketAssignSeatInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ticket_type_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = ticketTypeList[position]
        holder.ticketIDInfo.text = "Ticket ID: ${ticket.ticketId}"
        holder.ticketTypeInfo.text = "Ticket type: ${ticket.ticketType}"
        holder.ticketPriceInfo.text = "Ticket price: ${ticket.ticketPrice.toString()}"
        holder.ticketLimitInfo.text = "Ticket max waiting list: ${ticket.maxWaitingList.toString()}"
        holder.ticketRemainingInfo.text = "Ticket remaining: ${ticket.ticketRemaining.toString()}"
        holder.ticketPromotionCodeInfo.text = "Ticket promotion code: ${ticket.promotionCode}"
        holder.ticketPromotionExpiryInfo.text = "Ticket promotion expired: ${ticket.expiryDate}"
        holder.ticketDiscountInfo.text = "Ticket discount: ${ticket.discount.toString()}%"
        holder.ticketAssignSeatInfo.text = "Assign seat: ${ticket.assignSeat?.joinToString(",")}"
    }

    override fun getItemCount(): Int {
        return ticketTypeList.size
    }
}