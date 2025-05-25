package com.example.eventify.attendees

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.Admin.AdminLoginPage
import com.example.eventify.ModelData.WaitingListModelData
import com.example.eventify.R
import com.example.eventify.attendeesAdapter.AttendeesWaitingListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class AttendeesWaitingList : Fragment() {
    private var user: FirebaseUser? = null
    private lateinit var view: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AttendeesWaitingListAdapter
    private lateinit var waitingList: ArrayList<WaitingListModelData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view =  inflater.inflate(R.layout.fragment_attendees_waiting_list, container, false)

        initializeUI()
        initializeListener()
        return view
    }


    private fun initializeUI() {
        val firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser
        if (user == null){
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), AdminLoginPage::class.java)
            startActivity(intent)
            requireActivity().finish()
        }else{
            user = firebaseAuth.currentUser
        }

        waitingList = ArrayList()
        recyclerView = view.findViewById(R.id.AttendeesWaitingListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = AttendeesWaitingListAdapter(waitingList)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        getWaitingListData()
    }


    private fun initializeListener() {
        adapter.setOnWaitingListOnClick(object: AttendeesWaitingListAdapter.waitingListOnClick{
            override fun onClick(waitingList: WaitingListModelData) {
                val dialog = AttendeesWaitingListDetailFragment()
                val bundle = Bundle()
                bundle.putParcelable("waiting_list_data",waitingList)
                dialog.arguments = bundle
                dialog.show(parentFragmentManager,"waitingListDetailFragment")
            }
        })
    }

    private fun getWaitingListData() {
        waitingList.clear()
        val waitingListDatabaseReference = FirebaseDatabase.getInstance().getReference("waiting_lists")
        waitingListDatabaseReference.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    for(data in snapshot.children){
                        val waitingListData = data.getValue(WaitingListModelData::class.java)

                        if(waitingListData != null && waitingListData.userId == user?.uid){
                            waitingList.add(WaitingListModelData(
                                waitingListData.waitingListId,
                                waitingListData.eventId,
                                waitingListData.userId,
                                waitingListData.email,
                                waitingListData.status,
                                waitingListData.notification_sent,
                                waitingListData.timeJoin
                            ))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Failed to fetch waiting list", error)
            }
    }
}