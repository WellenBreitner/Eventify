package com.example.eventify.attendeesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.WaitingListModelData
import com.example.eventify.R

class AttendeesWaitingListAdapter(
    private val waitingList: ArrayList<WaitingListModelData>
) : RecyclerView.Adapter<AttendeesWaitingListAdapter.ViewHolder>(){
    private lateinit var listener: waitingListOnClick

    fun setOnWaitingListOnClick(listener: waitingListOnClick){
        this.listener = listener
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val waitingListIndex: TextView = itemView.findViewById(R.id.attendeesWaitingListIndex)
        val waitingListId: TextView = itemView.findViewById(R.id.attendeesWaitingListID)
        val waitingListEmail: TextView = itemView.findViewById(R.id.attendeesWaitingListEmail)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attendees_event_waiting_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val waitingListData = waitingList[position]
        holder.waitingListIndex.text = "Waiting list ${position + 1}"
        holder.waitingListId.text = "Waiting list ID: ${waitingListData.waitingListId}"
        holder.waitingListEmail.text = "Waiting list email: ${waitingListData.email}"

        holder.itemView.setOnClickListener {
            listener.onClick(waitingListData)
        }
    }

    override fun getItemCount(): Int {
        return waitingList.size
    }

    interface waitingListOnClick{
        fun onClick(waitingList: WaitingListModelData)
    }
}