package com.example.alpas.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alpas.Constants
import com.example.alpas.R
import com.example.alpas.adapters.AlpasCommentAdapter
import com.example.alpas.adapters.AlpasNotificationListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Comment
import com.example.alpas.models.NotificationData
import com.example.alpas.models.SavedThread
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_thread_details.*
import kotlinx.android.synthetic.main.fragment_forum.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.util.ArrayList


class NotificationsFragment : BaseFragment() {

    private var notificationList = ArrayList<NotificationData>()
    private var mSavedThreadDetails =  ArrayList<SavedThread>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onResume() {
        super.onResume()
        readNotification()
        checkSavedThread()
    }

    private fun readNotification() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATION)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val notification = dataSnapShot.getValue(NotificationData::class.java)
                    if (notification != null) {
                        if (FirestoreClass().getCurrentUserID() == notification.to) {
                            notificationList.add(notification)
                        }
                    }
                }
                if (rv_notification_items != null) {
                    if (notificationList.size > 0) {
                        rv_notification_items.visibility = View.VISIBLE
                        tv_no_notification_found.visibility = View.GONE

                        rv_notification_items.layoutManager =
                            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
                        rv_notification_items.setHasFixedSize(true)
                        notificationList.reverse()
                        val notificationAdapter = AlpasNotificationListAdapter(
                            requireActivity(),
                            notificationList,
                            mSavedThreadDetails,
                            this@NotificationsFragment
                        )
                        rv_notification_items.adapter = notificationAdapter
                    }
                } else {
                    if (rv_notification_items != null) {
                        rv_notification_items.visibility = View.GONE
                        tv_no_notification_found.visibility = View.VISIBLE
                    }
                }

            }
        })
    }
    private fun checkSavedThread(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getSavedThreadsListToCheck(this)
    }
    fun successGettingSavedThreads(savedThread: ArrayList<SavedThread>){
        hideProgressDialog()
        mSavedThreadDetails = savedThread
    }
}