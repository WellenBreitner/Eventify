package com.example.eventify.eoAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.collection.LLRBNode.Color

class EOAdapter(
    private val dataList: ArrayList<EventModelData>,
    private val onCardClick: (EventModelData, TicketModelData, Int) -> Unit,
    private val onEditClick: (EventModelData) -> Unit,
    private val onAddTicketClick: (EventModelData) -> Unit
) : RecyclerView.Adapter<EOAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.eo_event_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        currentItem.eventImage?.let { holder.rvImage.setImageResource(it) }
        holder.rvName.text = currentItem.eventName
        holder.rvDate.text = currentItem.eventDate
        holder.rvLocation.text = currentItem.eventLocation

        holder.itemView.setOnClickListener {
            onCardClick(currentItem, TicketModelData(/*…*/), 0)
        }
        // button tap
        holder.rvEditButton.setOnClickListener {
            onEditClick(currentItem)
        }
        holder.rvTicketSetupButton.setOnClickListener {
            onAddTicketClick(currentItem) // ✅ handle new ticket button
        }

    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView){
        val rvImage:ImageView = itemView.findViewById(R.id.eoListEventImage)
        val rvName:TextView = itemView.findViewById(R.id.eoListEventName)
        val rvDate:TextView = itemView.findViewById(R.id.eoListEventDate)
        val rvLocation:TextView = itemView.findViewById(R.id.eoListLocation)
        val rvEditButton:Button = itemView.findViewById(R.id.eoEditButton)
        val rvTicketSetupButton:Button = itemView.findViewById(R.id.eoAddTicketTypeButton)
    }
}