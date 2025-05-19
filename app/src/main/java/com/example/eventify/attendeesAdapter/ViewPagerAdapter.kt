package com.example.eventify.attendeesAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.eventify.attendees.AttendeesBookedEvent
import com.example.eventify.attendees.AttendeesEventList
import com.example.eventify.attendees.AttendeesWaitingList

class ViewPagerAdapter(fragmentManager: FragmentManager,
                       lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> AttendeesEventList()
            1 -> AttendeesBookedEvent()
            2 -> AttendeesWaitingList()
            else -> AttendeesEventList()
        }
    }
}